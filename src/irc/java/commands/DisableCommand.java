package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class DisableCommand extends IRCCommand {

	public DisableCommand(CommandHandler commandHandler) {
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
					if(command.isEnabled()) {
						command.setEnabled(false);
						messageHandler.sendMessage(target, "Command disabled: "
								+ commandName);
					} else
						messageHandler.sendMessage(target, commandName
								+ " is already disabled");
					break;
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Disable Command";
	}

	@Override
	public String getUsage() {
		return "DISABLE <command>";
	}

	@Override
	public String getCommandName() {
		return "DISABLE ";
	}

	@Override
	public String getDescription() {
		return "Disables the given command";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.OWNER;
	}
}
