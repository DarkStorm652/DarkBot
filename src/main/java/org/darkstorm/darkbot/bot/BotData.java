package org.darkstorm.darkbot.bot;

import joptsimple.OptionSet;

public abstract class BotData {
	public abstract void parse(OptionSet options);

	public abstract boolean isValid();
}
