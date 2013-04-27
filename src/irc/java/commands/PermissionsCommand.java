package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class PermissionsCommand extends IRCCommand {

	public PermissionsCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		String messageLC = messageText.toLowerCase();
		PermissionsHandler permissionsHandler = bot.getPermissionsHandler();
		MessageHandler messageHandler = bot.getMessageHandler();
		if(messageLC.startsWith(" add ")) {
			String nickname = messageText.substring(5);
			permissionsHandler.addPrivilegedNick(nickname);
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"Permission added: " + nickname);
		} else if(messageLC.startsWith(" del")) {
			String nickname = messageText.substring(5);
			boolean removed = permissionsHandler.removePrivilegedNick(nickname);
			if(removed)
				messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
						"Permission removed: " + nickname);
			else
				messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
						"No permissions found for " + nickname);
		} else if(messageLC.trim().equals("")) {
			String[] privileged = permissionsHandler.getPrivilegedNicknames();
			String permissions = "None";
			if(privileged.length != 0) {
				permissions = privileged[0];
				for(int i = 1; i < privileged.length; i++)
					permissions += ", " + privileged[i];
			}
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"Permissions: " + permissions);
		}
	}

	@Override
	public String getName() {
		return "Permissions Commands";
	}

	@Override
	public String getCommandName() {
		return "PERMS";
	}

	@Override
	public String getUsage() {
		return "PERMS [ADD|DEL] [nickname]";
	}

	@Override
	public String getDescription() {
		return "Gives or removes permissions for a specified person";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.OWNER;
	}

}
