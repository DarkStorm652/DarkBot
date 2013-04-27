package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;

public class MessageCommand extends IRCCommand {
	public MessageCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		MessageHandler messageHandler = bot.getMessageHandler();
		String messageText = userMessage.getMessage();
		if(!messageText.contains(" "))
			return;
		String channelOrUser = messageText.split(" ")[0];
		messageHandler.sendMessage(channelOrUser, messageText
				.substring(channelOrUser.length() + 1));
	}

	@Override
	public String getName() {
		return "Message Command";
	}

	@Override
	public String getUsage() {
		return "MSG <channel|nick> <message>";
	}

	@Override
	public String getCommandName() {
		return "MSG ";
	}

	@Override
	public String getDescription() {
		return "Makes the bot say the string of words "
				+ "in a specific channel or to a user";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}

}
