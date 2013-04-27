package org.darkstorm.darkbot.minecraftbot.logging;

import java.util.Date;

import java.text.*;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.bot.Nameable;
import org.darkstorm.darkbot.minecraftbot.*;

public class MinecraftSystemLogger extends MinecraftLogger {
	public MinecraftSystemLogger(MinecraftBot bot) {
		super(bot);
	}

	private DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss:SSS");
	private Date date = new Date();

	@Override
	public void log(MinecraftLogType logType, String message) {
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
	public void log(Object source, MinecraftLogType logType, String message) {
		String dateBlock = createDateBlock();
		String sourceBlock = createSourceBlock(source);
		String fullMessage = dateBlock + " " + logType + " " + sourceBlock
				+ ": " + message;
		printMessage(logType, fullMessage);
	}

	private String createSourceBlock(Object source) {
		StringBuilder messageStart = new StringBuilder();
		messageStart.append("[");
		if(source instanceof MinecraftBot)
			messageStart.append(bot.getName());
		else if(source instanceof MinecraftBotAccessor) {
			MinecraftBot bot = ((MinecraftBotAccessor) source).getBot();
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

	private void printMessage(MinecraftLogType logType, String message) {
		DarkBot darkBot = bot.getDarkBot();
		switch(logType) {
		case MESSAGE:
		case WARNING:
		case OTHER:
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
