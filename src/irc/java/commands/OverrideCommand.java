package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class OverrideCommand extends IRCCommand {

	public OverrideCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		PermissionsHandler permissionsHandler = bot.getPermissionsHandler();
		permissionsHandler.setOwner(userMessage.getSender().getNickname());
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
				"Controls overrided! " + userMessage.getSender().getNickname()
						+ " is now the owner");
	}

	@Override
	public String getName() {
		return "Override Command";
	}

	@Override
	public String getCommandName() {
		return "OVERRIDE";
	}

	@Override
	public String getUsage() {
		return "OVERRIDE";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.ORIGINAL_OWNER;
	}

	@Override
	public String getDescription() {
		return "Overrides ownership controls";
	}
}
