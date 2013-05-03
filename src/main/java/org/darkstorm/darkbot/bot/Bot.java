package org.darkstorm.darkbot.bot;

import org.darkstorm.darkbot.DarkBot;

public abstract class Bot implements Nameable {
	protected final DarkBot darkBot;

	public Bot(DarkBot darkBot) {
		if(darkBot == null)
			throw new IllegalArgumentException(
					"param 0 (type DarkBot) is invalid");
		this.darkBot = darkBot;
	}

	public abstract boolean isConnected();

	@Override
	public String getName() {
		return getClass().getAnnotation(BotManifest.class).name();
	}

	public DarkBot getDarkBot() {
		return darkBot;
	}
}
