package org.darkstorm.darkbot.ircbot.handlers;

import org.darkstorm.darkbot.bot.Nameable;
import org.darkstorm.darkbot.ircbot.*;
import org.darkstorm.darkbot.ircbot.logging.IRCLogger;

public abstract class IRCHandler implements Nameable, IRCBotAccessor {
	protected IRCBot bot;
	protected IRCLogger logger;

	public IRCHandler(IRCBot bot) {
		this.bot = bot;
		logger = bot.getLogger();
	}

	public IRCBot getBot() {
		return bot;
	}
}
