package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.CommandHandler;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;

public class QuitCommand extends IRCCommand {

	public QuitCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		messageText = messageText.trim();
		String originalMessage = bot.getQuitMessage();
		if(messageText.length() > 0)
			bot.setQuitMessage(messageText);
		bot.disconnect();
		bot.setQuitMessage(originalMessage);
	}

	@Override
	public String getName() {
		return "Quit Command";
	}

	@Override
	public String getCommandName() {
		return "QUIT";
	}

	@Override
	public String getUsage() {
		return "QUIT [message]";
	}

	@Override
	public String getDescription() {
		return "Makes the bot quit";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.OWNER;
	}
}
