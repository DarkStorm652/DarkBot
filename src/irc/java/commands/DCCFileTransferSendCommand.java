package commands;

import java.io.File;
import java.util.*;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.dcc.DCCFileTransfer;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class DCCFileTransferSendCommand extends IRCCommand {

	public DCCFileTransferSendCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		MessageHandler messageHandler = bot.getMessageHandler();
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		String[] args = messageText.split(" ");
		if(args.length < 1) {
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"Not enough arguments");
			return;
		}
		File file = new File(args[0]);
		if(!file.exists()) {
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"File does not exist!");
			return;
		}
		List<Integer> ports = new ArrayList<Integer>();
		for(int i = 1; i < args.length; i++) {
			try {
				Integer port = new Integer(args[i]);
				ports.add(port);
			} catch(NumberFormatException exception) {
				messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
						"Invalid port: " + args[i]);
			}
		}
		int[] portsArray = new int[ports.size()];
		for(int i = 0; i < portsArray.length; i++)
			portsArray[i] = ports.get(i);
		new DCCFileTransfer(bot, file, userMessage.getSender().getNickname(),
				30000, portsArray).doSend(false);
	}

	@Override
	public String getName() {
		return "DCC File Transfer Send Command";
	}

	@Override
	public String getDescription() {
		return "Sends a file over DCC";
	}

	@Override
	public String getCommandName() {
		return "SEND ";
	}

	@Override
	public String getUsage() {
		return "SEND <file> [port...]";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.OWNER;
	}

}
