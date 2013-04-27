package org.darkstorm.darkbot.minecraftbot.handlers;

import org.darkstorm.darkbot.bot.Nameable;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.logging.MinecraftLogger;

public abstract class MinecraftHandler implements Nameable,
		MinecraftBotAccessor {
	protected final MinecraftBot bot;
	protected final MinecraftLogger logger;

	public MinecraftHandler(MinecraftBot bot) {
		this.bot = bot;
		logger = bot.getLogger();
	}

	@Override
	public MinecraftBot getBot() {
		return bot;
	}
}
