package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class OwnerCommand extends IRCCommand {

	public OwnerCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String nick = userMessage.getMessage();
		if(!nick.startsWith(" "))
			nick = userMessage.getSender().getNickname();
		else
			nick = nick.substring(1);
		if(nick.contains(" "))
			return;
		PermissionsHandler permissionsHandler = bot.getPermissionsHandler();
		if(permissionsHandler.isPermitted(nick, Permissions.PRIVILEGED)) {
			permissionsHandler.setOwner(nick);
			MessageHandler messageHandler = bot.getMessageHandler();
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"Ownership changed to " + nick);
		}
	}

	@Override
	public String getName() {
		return "Owner Command";
	}

	@Override
	public String getCommandName() {
		return "OWNER";
	}

	@Override
	public String getUsage() {
		return "OWNER [nickname]";
	}

	@Override
	public String getDescription() {
		return "Changes ownership of this bot to the specified person";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}
}
