package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class InviteCommand extends IRCCommand {

	public InviteCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		MessageType type = message.getType();
		if(type.equals(MessageType.INVITE)) {
			InviteMessage invite = (InviteMessage) message;
			Channel channel = invite.getChannel();
			channel.join();
			MessageHandler messageHandler = bot.getMessageHandler();
			messageHandler.sendMessage(channel.getName(),
					"Joined upon invite by " + invite.getSenderNickname());
		}
	}

	@Override
	public String getName() {
		return "Invite";
	}

	@Override
	public String getDescription() {
		return "Enters a channel upon invite";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}

}
