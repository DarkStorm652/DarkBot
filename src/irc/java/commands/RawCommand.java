package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class RawCommand extends IRCCommand {

	public RawCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		if(messageText.length() > 0) {
			MessageHandler messageHandler = bot.getMessageHandler();
			if(!messageHandler.sendRaw(messageText))
				messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
						"Error sending!");
		}
	}

	@Override
	public String getName() {
		return "Raw Command";
	}

	@Override
	public String getCommandName() {
		return "RAW ";
	}

	@Override
	public String getUsage() {
		return "RAW [message]";
	}

	@Override
	public String getDescription() {
		return "Sends a raw message";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}

}
