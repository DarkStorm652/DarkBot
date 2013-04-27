package org.darkstorm.darkbot.tools;

import java.lang.reflect.*;
import java.util.ArrayList;

import joptsimple.OptionSpec;

import org.darkstorm.darkbot.bot.*;

public class ClassRepository {
	private static ArrayList<BotInfo> bots;
	private static Object botsLock = new Object();

	static {
		BotLoader botLoader = new BotLoader();
		botLoader.loadBots();
		initBots(botLoader.getBotClasses());
	}

	private ClassRepository() {
	}

	public static void initBots(Class<?>[] botClasses) {
		synchronized(botsLock) {
			bots = new ArrayList<BotInfo>();
			for(Class<?> botClass : botClasses) {
				try {
					bots.add(BotInfo.parse(botClass));
				} catch(Exception exception) {
					exception.printStackTrace();
					System.err.println("Failed to load bot "
							+ botClass.getName());
				}
			}
		}
	}

	public static BotInfo getBot(String name) {
		synchronized(botsLock) {
			for(BotInfo bot : bots)
				if(name.equalsIgnoreCase(bot.getName()))
					return bot;
			return null;
		}
	}

	public static BotInfo[] getBots() {
		synchronized(botsLock) {
			return bots.toArray(new BotInfo[bots.size()]);
		}
	}

	public static class BotInfo {
		private String name;
		private Class<? extends Bot> botClass;
		private Class<? extends BotData> botDataClass;
		private OptionSpec<?>[] arguments;

		private BotInfo(String name, Class<? extends Bot> botClass,
				Class<? extends BotData> botDataClass, OptionSpec<?>[] arguments) {
			this.name = name;
			this.botClass = botClass;
			this.botDataClass = botDataClass;
			this.arguments = arguments;
		}

		public static BotInfo parse(Class<?> classToParse) {
			try {
				if(!Bot.class.isAssignableFrom(classToParse))
					throw new IllegalArgumentException();
				Class<? extends Bot> botClass = classToParse
						.asSubclass(Bot.class);
				BotManifest manifest = botClass
						.getAnnotation(BotManifest.class);
				if(manifest == null)
					throw new IllegalArgumentException("No manifest attribute");
				String name = manifest.name();
				Class<? extends BotData> botDataClass = manifest.botDataClass();
				Method optionsMethod = null;
				for(Method method : botDataClass.getMethods())
					if(method.getAnnotation(BotArgumentHandler.class) != null) {
						if(optionsMethod != null)
							throw new IllegalArgumentException(
									"Too many options handler methods");
						optionsMethod = method;
					}
				if(optionsMethod == null
						|| !Modifier.isStatic(optionsMethod.getModifiers())
						|| !OptionSpec[].class.isAssignableFrom(optionsMethod
								.getReturnType()))
					throw new IllegalArgumentException(
							"Invalid options handler method");
				OptionSpec<?>[] options = (OptionSpec<?>[]) optionsMethod
						.invoke(null);
				return new BotInfo(name, botClass, botDataClass, options);
			} catch(Throwable exception) {
				if(exception instanceof IllegalArgumentException)
					throw (IllegalArgumentException) exception;
				throw new IllegalArgumentException(exception);
			}
		}

		public String getName() {
			return name;
		}

		public Class<? extends Bot> getBotClass() {
			return botClass;
		}

		public Class<? extends BotData> getBotDataClass() {
			return botDataClass;
		}

		public OptionSpec<?>[] getArguments() {
			return arguments;
		}
	}
}
