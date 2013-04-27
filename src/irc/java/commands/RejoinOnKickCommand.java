package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.irc.messages.*;

public class RejoinOnKickCommand extends IRCCommand {

	public RejoinOnKickCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof KickMessage))
			return;
		KickMessage kickMessage = (KickMessage) message;
		String target = kickMessage.getTargetNickname();
		NicknameHandler nicknameHandler = bot.getNicknameHandler();
		if(target.equals(nicknameHandler.getNickname()))
			kickMessage.getChannel().join();
	}

	@Override
	public String getName() {
		return "Rejoin On Kick";
	}

	@Override
	public String getDescription() {
		return "Rejoins when kicked";
	}

}
