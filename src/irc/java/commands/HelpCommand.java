package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.irc.messages.*;

public class HelpCommand extends IRCCommand {

	public HelpCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		if(messageText.startsWith(" ") && messageText.length() > 1) {
			String commandName = messageText.substring(1);
			for(IRCCommand command : commandHandler.getCommands()) {
				String nameForCommand = command.getCommandName();
				if(nameForCommand != null) {
					nameForCommand = nameForCommand.trim();
					if(commandName.equalsIgnoreCase(nameForCommand)) {
						MessageHandler messageHandler = bot.getMessageHandler();
						messageHandler.setFloodControlEnabled(false);
						String sender = userMessage.getSender().getNickname();
						messageHandler.sendNotice(sender,
								"Command: " + command.getName());
						messageHandler.sendNotice(sender, " ");
						messageHandler.sendNotice(sender, "Usage:");
						messageHandler.sendNotice(sender,
								"	" + commandHandler.getCommandActivator()
										+ command.getUsage());
						messageHandler.sendNotice(sender, " ");
						messageHandler.sendNotice(sender, "Description:");
						messageHandler.sendNotice(sender,
								"	" + command.getDescription());
						messageHandler.setFloodControlEnabled(true);
						break;
					}
				}
			}
			return;
		}
		String sender = userMessage.getSender().getNickname();
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.setFloodControlEnabled(false);
		messageHandler.sendNotice(sender, "Commands Available: ");
		PermissionsHandler permissionsHandler = bot.getPermissionsHandler();
		for(IRCCommand command : commandHandler.getCommands()) {
			if(command.getCommandName() != null
					&& permissionsHandler.isPermitted(sender,
							command.getPermissions()))
				messageHandler.sendNotice(sender, " - "
						+ command.getCommandName().trim());
		}
		messageHandler.sendNotice(sender,
				"Commands are non-case sensitive, except for their arguments");
		messageHandler.sendNotice(sender,
				"Use '" + commandHandler.getCommandActivator()
						+ "help <command>' for more info on a command");
		messageHandler.setFloodControlEnabled(true);
	}

	@Override
	public String getName() {
		return "Help Command";
	}

	@Override
	public String getCommandName() {
		return "HELP";
	}

	@Override
	public String getUsage() {
		return "HELP [command]";
	}

	@Override
	public String getDescription() {
		return "Shows help";
	}

}
