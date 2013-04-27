package org.darkstorm.darkbot.bot;

public abstract class Logger {
	public abstract void log(String message);

	public abstract void log(Object source, String message);
}
