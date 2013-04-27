package org.darkstorm.darkbot.minecraftbot.logging;

import org.darkstorm.darkbot.bot.Logger;
import org.darkstorm.darkbot.ircbot.logging.IRCLogger.IRCLogType;
import org.darkstorm.darkbot.minecraftbot.*;

public abstract class MinecraftLogger extends Logger implements
		MinecraftBotAccessor {
	public static enum MinecraftLogType {
		MESSAGE("MESSAGE"),
		WARNING("WARNING"),
		ERROR("ERROR"),
		DEBUG("DEBUG"),
		DEBUG_ERROR("ERROR_DEBUG"),
		OTHER("OTHER");

		private String name;

		MinecraftLogType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	protected MinecraftBot bot;

	public MinecraftLogger(MinecraftBot bot) {
		this.bot = bot;
	}

	@Override
	public void log(String message) {
		log(IRCLogType.MESSAGE, message);
	}

	@Override
	public void log(Object source, String message) {
		log(source, MinecraftLogType.MESSAGE, message);
	}

	public abstract void log(MinecraftLogType logType, String message);

	public abstract void log(Object source, MinecraftLogType logType,
			String message);

	public void logException(Object source, MinecraftLogType logType,
			Throwable exception) {
		log(source, logType, exception.toString());
		StackTraceElement[] trace = exception.getStackTrace();
		for(StackTraceElement element : trace)
			log(source, logType, "\tat " + element.toString());
		Throwable cause = exception.getCause();
		if(cause != null)
			logCause(source, logType, cause, trace);
	}

	private void logCause(Object source, MinecraftLogType logType,
			Throwable cause, StackTraceElement[] causedTrace) {
		StackTraceElement[] trace = cause.getStackTrace();
		int m = trace.length - 1, n = causedTrace.length - 1;
		while(m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
			m--;
			n--;
		}
		int framesInCommon = trace.length - 1 - m;

		log(source, logType, "Caused by: " + this);
		for(int i = 0; i <= m; i++)
			log(source, logType, "\tat " + trace[i]);
		if(framesInCommon != 0)
			log(source, logType, "\t... " + framesInCommon + " more");

		Throwable causesCause = cause.getCause();
		if(causesCause != null)
			logCause(source, logType, causesCause, trace);
	}

	public MinecraftBot getBot() {
		return bot;
	}

}
