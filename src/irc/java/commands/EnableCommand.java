package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class EnableCommand extends IRCCommand {

	public EnableCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String commandName = userMessage.getMessage();
		commandName = commandName.toLowerCase();
		for(IRCCommand command : commandHandler.getCommands()) {
			String nameForCommand = command.getCommandName();
			if(nameForCommand != null) {
				nameForCommand = nameForCommand.toLowerCase();
				nameForCommand = nameForCommand.trim();
				if(commandName.equals(nameForCommand)) {
					MessageHandler messageHandler = bot.getMessageHandler();
					String target = Tools.getCorrectTarget(userMessage);
					if(!command.isEnabled()) {
						command.setEnabled(true);
						messageHandler.sendMessage(target, "Command enabled: "
								+ commandName);
					} else
						messageHandler.sendMessage(target, commandName
								+ " is already enabled");
					break;
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Enable Command";
	}

	@Override
	public String getUsage() {
		return "ENABLE <command>";
	}

	@Override
	public String getCommandName() {
		return "ENABLE ";
	}

	@Override
	public String getDescription() {
		return "Enables the given command";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.OWNER;
	}
}
