package org.darkstorm.darkbot.ircbot.util;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import org.darkstorm.darkbot.bot.Nameable;
import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.commands.defaults.*;
import org.darkstorm.darkbot.ircbot.handlers.CommandHandler;
import org.darkstorm.darkbot.ircbot.logging.*;
import org.darkstorm.darkbot.ircbot.logging.IRCLogger.IRCLogType;

public class IRCCommandLoader implements Nameable {
	private CommandHandler commandHandler;
	private IRCLogger logger;

	public IRCCommandLoader(CommandHandler commandHandler) {
		if(commandHandler == null)
			throw new NullPointerException();
		this.commandHandler = commandHandler;
		IRCBot bot = commandHandler.getBot();
		logger = bot.getLogger();
	}

	public void loadCommands() {
		commandHandler.removeAllCommands();
		loadDefaultCommands();
		loadJavaCommands();
		loadPythonCommands();
	}

	private void loadDefaultCommands() {
		commandHandler.addCommand(new PingCommand(commandHandler));
		logger.log(this, IRCLogType.DEBUG, "Command loaded: Ping");
		commandHandler.addCommand(new VersionCommand(commandHandler));
		logger.log(this, IRCLogType.DEBUG, "Command loaded: Version");
	}

	private void loadJavaCommands() {
		try {
			URL commandsDir = getClass().getResource("/commands");
			if(commandsDir == null)
				return;
			URLClassLoader classLoader = new URLClassLoader(
					new URL[] { commandsDir },
					ClassLoader.getSystemClassLoader());
			for(URL file : getSubURLs(commandsDir)) {
				try {
					String fileName = file.getPath();
					if(!fileName.endsWith(".class"))
						continue;
					fileName = fileName.substring(commandsDir.getFile()
							.length() - "commands".length());
					fileName = fileName.substring(0, fileName.length() - 6);
					fileName = fileName.replace('/', '.');
					Class<?> commandClass = classLoader.loadClass(fileName);
					if(!IRCCommand.class.isAssignableFrom(commandClass)) {
						continue;
					}
					Constructor<?> constructor = commandClass
							.getConstructor(CommandHandler.class);
					IRCCommand command = (IRCCommand) constructor
							.newInstance(commandHandler);
					commandHandler.addCommand(command);
					logger.log(this, IRCLogType.DEBUG, "Command loaded: "
							+ command.getName());
				} catch(Exception exception) {
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
				}
			}
		} catch(Exception exception) {
			logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
		}
	}

	private URL[] getSubURLs(URL url) throws IOException {
		ArrayList<URL> urls = new ArrayList<URL>();
		String scheme = url.getProtocol();
		if(scheme.equals("jar")) {
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile archive = con.getJarFile();
			Enumeration<JarEntry> entries = archive.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if(entry.getName().startsWith(url.getPath().split("!/")[1])) {
					urls.add(getClass().getResource("/" + entry.getName()));
				}
			}
		} else if(scheme.equals("file")) {
			File file = new File(url.getFile());
			for(File subFile : file.listFiles()) {
				URL subURL = subFile.toURI().toURL();
				urls.add(subURL);
				if(subFile.isDirectory())
					urls.addAll(Arrays.asList(getSubURLs(subURL)));
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	private void loadPythonCommands() {

	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	@Override
	public String getName() {
		return "IRCCommandLoader";
	}
}
