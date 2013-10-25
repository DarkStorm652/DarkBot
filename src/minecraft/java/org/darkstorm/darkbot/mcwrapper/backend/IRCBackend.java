package org.darkstorm.darkbot.mcwrapper.backend;

import java.util.regex.*;

import org.darkstorm.darkbot.ircbot.*;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;
import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.mcwrapper.commands.CommandException;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventHandler;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.ChatReceivedEvent;

public class IRCBackend implements Backend, EventListener {
	private final MinecraftBotWrapper mcBot;
	private final IRCBot ircBot;
	private final MCBotCommand command;

	public IRCBackend(MinecraftBotWrapper mcBot, IRCBotData data) {
		this.mcBot = mcBot;
		ircBot = (IRCBot) MinecraftBotWrapper.getDarkBot().createBot(data);
		command = new MCBotCommand(ircBot.getCommandHandler());
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		ircBot.setQuitMessage(event.getReason());
		ircBot.disconnect();
	}

	@Override
	public void enable() {
		MinecraftBot bot = mcBot.getBot();
		ircBot.getCommandHandler().addCommand(command);
		bot.getEventManager().registerListener(this);
	}

	@Override
	public void say(String message) {
		ircBot.getMessageHandler().setFloodControlEnabled(false);
		for(Channel channel : ircBot.getChannelHandler().getChannels())
			ircBot.getMessageHandler().sendMessage(channel.getName(), "[BOT] " + message);
		ircBot.getMessageHandler().setFloodControlEnabled(true);
	}

	@Override
	public void disable() {
		MinecraftBot bot = mcBot.getBot();
		ircBot.getCommandHandler().removeCommand(command);
		bot.getEventManager().unregisterListener(this);
	}

	@EventHandler
	public void onChatReceived(ChatReceivedEvent event) {
		ircBot.getMessageHandler().setFloodControlEnabled(false);
		for(Channel channel : ircBot.getChannelHandler().getChannels())
			ircBot.getMessageHandler().sendMessage(channel.getName(), "[CHAT] \u000F\u000300" + mcToIRCColors(event.getMessage()));
		ircBot.getMessageHandler().setFloodControlEnabled(true);
	}

	private String mcToIRCColors(String message) {
		Pattern pattern = Pattern.compile("§[0-9a-fk-or]");
		Matcher matcher = pattern.matcher(message);
		while(matcher.find())
			message = message.replace(matcher.group(), convertMCColor(matcher.group()));
		return message;
	}

	private String convertMCColor(String color) {
		char value = color.charAt(1);
		switch(value) {
		case '0':
			return "\u000F\u000301";
		case '1':
			return "\u000F\u000302";
		case '2':
			return "\u000F\u000303";
		case '3':
			return "\u000F\u000310";
		case '4':
			return "\u000F\u000305";
		case '5':
			return "\u000F\u000306";
		case '6':
			return "\u000F\u000308";
		case '7':
			return "\u000F\u000315";
		case '8':
			return "\u000F\u000314";
		case '9':
			return "\u000F\u000312";
		case 'a':
			return "\u000F\u000309";
		case 'b':
			return "\u000F\u000311";
		case 'c':
			return "\u000F\u000304";
		case 'd':
			return "\u000F\u000304";
		case 'e':
			return "\u000F\u000308";
		case 'f':
			return "\u000F\u000300";
		case 'k':
			return "▒";
		case 'l':
			return "\u0002";
		case 'm':
			return "";
		case 'n':
			return "\u001F";
		case 'o':
			return "";
		case 'r':
			return "\u000F";
		default:
			return "";
		}
	}

	public MinecraftBotWrapper getMCBot() {
		return mcBot;
	}

	public IRCBot getIRCBot() {
		return ircBot;
	}

	public class MCBotCommand extends IRCCommand {
		private MCBotCommand(CommandHandler commandHandler) {
			super(commandHandler);
		}

		@Override
		public void execute(Message message) {
			if(!(message instanceof UserMessage))
				return;
			UserMessage userMessage = (UserMessage) message;
			try {
				mcBot.getCommandManager().execute(userMessage.getMessage());
				ircBot.getMessageHandler().sendMessage(Tools.getCorrectTarget(userMessage), "Command executed: " + userMessage.getMessage());
			} catch(CommandException e) {
				StringBuilder error = new StringBuilder("Error: ");
				if(e.getCause() != null)
					error.append(e.getCause().toString());
				else if(e.getMessage() == null)
					error.append("null");
				if(e.getMessage() != null) {
					if(e.getCause() != null)
						error.append(": ");
					error.append(e.getMessage());
				}
				ircBot.getMessageHandler().sendMessage(Tools.getCorrectTarget(userMessage), error.toString());
			}
		}

		@Override
		public String getName() {
			return "MCBot Controls";
		}

		@Override
		public String getDescription() {
			return "Manages an MC Bot";
		}

		@Override
		public String getCommandName() {
			return "MCBOT ";
		}

		@Override
		public Permissions getPermissions() {
			return Permissions.OWNER;
		}

		@Override
		public String getUsage() {
			return "MCBOT [bot command]";
		}
	}

}
