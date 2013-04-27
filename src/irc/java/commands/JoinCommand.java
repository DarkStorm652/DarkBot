package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class JoinCommand extends IRCCommand {

	public JoinCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		if(!Channel.isChannel(messageText) || messageText.contains(" "))
			return;
		ChannelHandler channelHandler = bot.getChannelHandler();
		Channel channel = channelHandler.newChannel(messageText);
		MessageHandler messageHandler = bot.getMessageHandler();
		if(channel != null) {
			channel.join();
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"Joined " + messageText);
		} else
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"Already in " + messageText);
	}

	@Override
	public String getName() {
		return "Join Command";
	}

	@Override
	public String getCommandName() {
		return "JOIN ";
	}

	@Override
	public String getUsage() {
		return "JOIN [channel]";
	}

	@Override
	public String getDescription() {
		return "Joins a specified channel";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}

}
