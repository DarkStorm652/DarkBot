package commands;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.ircbot.IRCBotData;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.*;

public class ServerCommand extends IRCCommand {

	public ServerCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String[] parts = userMessage.getMessage().split(" ");

		MessageHandler messageHandler = bot.getMessageHandler();
		String target = Tools.getCorrectTarget(userMessage);

		if(parts.length > 3) {
			messageHandler.sendMessage(target, "Invalid argument count");
			return;
		} else {
			for(String part : parts) {
				if(part.isEmpty()) {
					messageHandler.sendMessage(target, "Invalid argument");
					return;
				}
			}
		}

		String host = parts[0];
		if(host.isEmpty()) {
			messageHandler.sendMessage(target, "Invalid host");
			return;
		}

		int port = 6667;
		if(parts.length > 1) {
			try {
				port = Integer.parseInt(parts[1]);
			} catch(NumberFormatException exception) {
				messageHandler.sendMessage(target, "Invalid port");
				return;
			}
		}

		String owner = bot.getPermissionsHandler().getOriginalOwner();
		if(parts.length > 2) {
			owner = parts[2];
		}

		IRCBotData data = new IRCBotData();
		data.nickname = bot.getNicknameHandler().getOriginalNickname();
		data.owner = owner;
		data.password = bot.getNicknameHandler().getPassword();
		data.server = host;
		data.port = port;

		DarkBot darkBot = bot.getDarkBot();
		try {
			darkBot.createBot(data);
		} catch(Exception exception) {
			messageHandler.sendMessage(target, "Unable to create bot");
			return;
		}
		messageHandler.sendMessage(target, "Joined server!");
	}

	@Override
	public String getName() {
		return "Server Command";
	}

	@Override
	public String getDescription() {
		return "Connects a bot to the specified server";
	}

	@Override
	public String getCommandName() {
		return "SERVER ";
	}

	@Override
	public String getUsage() {
		return "SERVER <host> [port [owner]]";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.OWNER;
	}

}
