package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class SayCommand extends IRCCommand {
	public SayCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
				userMessage.getMessage());
	}

	@Override
	public String getName() {
		return "Say Command";
	}

	@Override
	public String getCommandName() {
		return "SAY ";
	}

	@Override
	public String getUsage() {
		return "SAY [dialog]";
	}

	@Override
	public String getDescription() {
		return "Makes the bot say the string of words";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}

}
