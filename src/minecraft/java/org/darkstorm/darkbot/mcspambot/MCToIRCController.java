package org.darkstorm.darkbot.mcspambot;

import java.util.regex.*;

import org.darkstorm.darkbot.ircbot.*;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventHandler;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet3Chat;

public class MCToIRCController implements EventListener {
	private final DarkBotMC mcBot;
	private final IRCBot ircBot;

	public MCToIRCController(DarkBotMC mcBot) {
		this.mcBot = mcBot;
		MinecraftBot bot = mcBot.getBot();
		IRCBotData botData = new IRCBotData();
		botData.nickname = "DarkBot[" + bot.getSession().getUsername() + "]";
		botData.password = "";
		botData.server = "evocraft.net";
		botData.port = 6667;
		botData.owner = bot.getOwner();
		botData.channels = new String[] { "#darkstorm" };
		ircBot = (IRCBot) DarkBotMC.DARK_BOT.createBot(botData);
		ircBot.getCommandHandler().addCommand(
				new MCBotCommand(ircBot.getCommandHandler()));
		bot.getEventManager().registerListener(this);
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		ircBot.setQuitMessage(event.getReason());
		ircBot.disconnect();
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet3Chat) {
			Packet3Chat chatPacket = (Packet3Chat) packet;
			ircBot.getMessageHandler().setFloodControlEnabled(false);
			for(Channel channel : ircBot.getChannelHandler().getChannels())
				ircBot.getMessageHandler().sendMessage(channel.getName(),
						"[CHAT] 00" + mcToIRCColors(chatPacket.message));
			ircBot.getMessageHandler().setFloodControlEnabled(true);
		}
	}

	private String mcToIRCColors(String message) {
		Pattern pattern = Pattern.compile("§[0-9a-fk-or]");
		Matcher matcher = pattern.matcher(message);
		while(matcher.find())
			message = message.replace(matcher.group(),
					convertMCColor(matcher.group()));
		return message;
	}

	private String convertMCColor(String color) {
		char value = color.charAt(1);
		switch(value) {
		case '0':
			return "01";
		case '1':
			return "02";
		case '2':
			return "03";
		case '3':
			return "10";
		case '4':
			return "05";
		case '5':
			return "06";
		case '6':
			return "08";
		case '7':
			return "15";
		case '8':
			return "14";
		case '9':
			return "12";
		case 'a':
			return "09";
		case 'b':
			return "11";
		case 'c':
			return "04";
		case 'd':
			return "04";
		case 'e':
			return "08";
		case 'f':
			return "00";
		case 'k':
			return "▒";
		case 'l':
			return "";
		case 'm':
			return "";
		case 'n':
			return "";
		case 'o':
			return "";
		case 'r':
			return "";
		default:
			return "";
		}
	}

	public DarkBotMC getMCBot() {
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
			ircBot.getMessageHandler().sendMessage(
					Tools.getCorrectTarget(userMessage),
					"Command executed: " + userMessage.getMessage());
			mcBot.onPacketProcess(new PacketProcessEvent(new Packet3Chat(
					"->[IRC] " + mcBot.getBot().getOwner() + " "
							+ userMessage.getMessage())));
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
