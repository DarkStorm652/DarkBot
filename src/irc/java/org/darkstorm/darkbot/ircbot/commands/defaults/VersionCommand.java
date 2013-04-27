package org.darkstorm.darkbot.ircbot.commands.defaults;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.irc.messages.*;

public class VersionCommand extends IRCCommand {
	public VersionCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(message instanceof UserMessage) {
			UserMessage userMessage = (UserMessage) message;
			String messageText = userMessage.getMessage();
			if(userMessage.isCTCP() && messageText.equals("VERSION")) {
				MessageHandler messageHandler = bot.getMessageHandler();
				messageHandler.sendCTCPNotice(userMessage.getSender()
						.getNickname(), "VERSION DarkBot " + DarkBot.VERSION);
			}
		}
	}

	@Override
	public String getName() {
		return "VERSION";
	}

	@Override
	public String getDescription() {
		return "Responds to VERSION commands. Cannot be disabled.";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
