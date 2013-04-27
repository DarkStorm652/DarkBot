package org.darkstorm.darkbot.ircbot;

import org.darkstorm.darkbot.bot.BotAccessor;

public interface IRCBotAccessor extends BotAccessor {
	@Override
	public IRCBot getBot();
}
