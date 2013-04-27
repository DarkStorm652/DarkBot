package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.CommandHandler;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.UserInfo;

public class PokebotCommand extends IRCCommand {

	public PokebotCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(message instanceof JoinMessage) {
			JoinMessage joinMessage = (JoinMessage) message;
			UserInfo user = joinMessage.getUser();
			if(user.getUsername().equals("~Pokebot"))
				bot.getMessageHandler().sendMessage(
						joinMessage.getChannel().getName(), "!catch");
		}
	}

	@Override
	public String getName() {
		return "Pokebot Commands";
	}

	@Override
	public String getDescription() {
		return "Handles Pokebot stuffs";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
