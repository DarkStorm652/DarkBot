package org.darkstorm.darkbot.tools;

import java.io.File;
import java.util.*;
import java.util.jar.*;

import org.darkstorm.darkbot.bot.Bot;

public class BotLoader {
	private Class<?>[] botClasses;

	public BotLoader() {
	}

	public void loadBots() {
		botClasses = loadBotsFromClasspath();
	}

	private Class<?>[] loadBotsFromClasspath() {
		File location = new File(BotLoader.class.getProtectionDomain()
				.getCodeSource().getLocation().getFile());
		if(location.isFile()) {
			String fileName = location.getName();
			if(fileName.endsWith(".jar"))
				return loadBotsFromClasspathJar(location);
		} else
			return loadBotsFromClasspathDirectory(location);
		return new Class<?>[0];
	}

	@SuppressWarnings("resource")
	private Class<?>[] loadBotsFromClasspathJar(File location) {
		ArrayList<Class<?>> botClasses = new ArrayList<Class<?>>();
		JarFile jarFile;
		try {
			jarFile = new JarFile(location);
		} catch(Throwable exception) {
			exception.printStackTrace();
			System.err.println("Failed to load bots from classpath");
			return null;
		}
		Enumeration<JarEntry> entries = jarFile.entries();
		while(entries.hasMoreElements()) {
			try {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if(!entryName.endsWith(".class"))
					continue;
				String entryClassName = entryName.replace('/', '.');
				entryClassName = entryClassName.substring(0,
						entryClassName.length() - 6);
				Class<?> entryClass = Class.forName(entryClassName);
				if(Bot.class.isAssignableFrom(entryClass)
						&& !Bot.class.equals(entryClass))
					botClasses.add(entryClass);
			} catch(Throwable exception) {
				exception.printStackTrace();
			}
		}
		return botClasses.toArray(new Class<?>[botClasses.size()]);
	}

	private Class<?>[] loadBotsFromClasspathDirectory(File directory) {
		ArrayList<Class<?>> botClasses = new ArrayList<Class<?>>();
		loadBotsInDirectory(botClasses, directory);
		return botClasses.toArray(new Class<?>[botClasses.size()]);
	}

	private void loadBotsInDirectory(ArrayList<Class<?>> botClasses,
			File directory) {
		for(File file : directory.listFiles()) {
			if(file.isFile()) {
				String filePath = file.getAbsolutePath();
				if(!filePath.endsWith(".class"))
					continue;
				try {
					File currentDirectory = new File(
							System.getProperty("user.dir"));
					for(File fileInCurrentDir : currentDirectory.listFiles()) {
						if(fileInCurrentDir.isDirectory()) {
							String fileName = fileInCurrentDir.getName();
							if(fileName.equals("bin")) {
								currentDirectory = fileInCurrentDir;
								break;
							}
						}
					}
					filePath = StringTools.splitFirst(filePath,
							currentDirectory.getName() + File.separator)[1];
					String className = filePath
							.replace(File.separatorChar, '.');
					className = className.substring(0, className.length() - 6);
					Class<?> entryClass = Class.forName(className);
					if(Bot.class.isAssignableFrom(entryClass)
							&& !Bot.class.equals(entryClass))
						botClasses.add(entryClass);
				} catch(Throwable exception) {
					exception.printStackTrace();
				}
			} else if(file.isDirectory())
				loadBotsInDirectory(botClasses, file);
		}
	}

	public Class<?>[] getBotClasses() {
		return botClasses;
	}
}
