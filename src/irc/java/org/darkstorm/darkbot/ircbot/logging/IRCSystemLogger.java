package org.darkstorm.darkbot.ircbot.logging;

import java.text.*;
import java.util.Date;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.bot.Nameable;
import org.darkstorm.darkbot.ircbot.*;

public class IRCSystemLogger extends IRCLogger {
	public IRCSystemLogger(IRCBot bot) {
		super(bot);
	}

	private DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss:SSS");
	private Date date = new Date();

	@Override
	public void log(IRCLogType logType, String message) {
		String dateBlock = createDateBlock();
		String threadNameBlock = createThreadNameBlock();
		String fullMessage = dateBlock + " " + logType + " " + threadNameBlock
				+ ": " + message;
		printMessage(logType, fullMessage);
	}

	private String createThreadNameBlock() {
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		return "[" + threadName + "]";
	}

	@Override
	public void log(Object source, IRCLogType logType, String message) {
		String dateBlock = createDateBlock();
		String sourceBlock = createSourceBlock(source);
		String fullMessage = dateBlock + " " + logType + " " + sourceBlock
				+ ": " + message;
		printMessage(logType, fullMessage);
	}

	private String createSourceBlock(Object source) {
		StringBuilder messageStart = new StringBuilder();
		messageStart.append("[");
		if(source instanceof IRCBot)
			messageStart.append("Bot:");
		if(source instanceof IRCBotAccessor) {
			IRCBot bot = ((IRCBotAccessor) source).getBot();
			messageStart.append(bot.getName());
			if(source instanceof Nameable)
				messageStart.append(":" + ((Nameable) source).getName());
			else
				messageStart.append(":(Unknown Source)");
		} else if(source instanceof Nameable)
			messageStart.append(((Nameable) source).getName());
		messageStart.append("]");
		return messageStart.toString();
	}

	private String createDateBlock() {
		date.setTime(System.currentTimeMillis());
		String formattedDate = dateFormat.format(date);
		return "[" + formattedDate + "]";
	}

	private void printMessage(IRCLogType logType, String message) {
		DarkBot darkBot = bot.getDarkBot();
		switch(logType) {
		case MESSAGE:
		case WARNING:
		case OTHER:
		case RAW:
			System.out.println(message);
			break;
		case DEBUG:
			if(darkBot.isDebugging())
				System.out.println(message);
			break;
		case DEBUG_ERROR:
			if(!darkBot.isDebugging())
				break;
		case ERROR:
			System.err.println(message);
		}
	}

}
