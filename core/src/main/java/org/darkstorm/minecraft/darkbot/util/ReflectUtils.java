package org.darkstorm.minecraft.darkbot.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

public final class ReflectUtils {
	private ReflectUtils() {
	}
	
	@SuppressWarnings("resource")
	public static List<Class<?>> getClassesInPackage(String packageName) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		URL packageURL;

		packageURL = classLoader.getResource(packageName.replace(".", "/"));

		if(packageURL.getProtocol().equals("jar")) {
			packageName = packageName.replace(".", "/");
			String jarFileName;
			JarFile jf;
			Enumeration<JarEntry> jarEntries;
			String entryName;

			// build jar file name, then loop through zipped entries
			jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
			jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
			jf = new JarFile(jarFileName);
			jarEntries = jf.entries();
			while(jarEntries.hasMoreElements()) {
				entryName = jarEntries.nextElement().getName();
				if(entryName.startsWith(packageName) && entryName.length() > packageName.length() + 5) {
					entryName = entryName.replace('/', '.');
					entryName = entryName.substring(0, entryName.lastIndexOf('.'));
					try {
						classes.add(Class.forName(entryName));
					} catch(ClassNotFoundException exception) {
						exception.printStackTrace();
					}
				}
			}

			// loop through files in classpath
		} else {
			try {
				Iterable<Class<?>> classesFromFile = getClasses(packageName);
				for(Class<?> c : classesFromFile)
					classes.add(c);
			} catch(ClassNotFoundException exception) {
				exception.printStackTrace();
			}
		}
		return classes;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and sub packages.
	 * 
	 * @author Paulius Matulionis - stackoverflow.com
	 * @param packageName
	 *            the base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 *             if class not found exception occurs
	 * @throws IOException
	 *             if IO error occurs
	 */
	private static Iterable<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		LinkedList<File> dirs = new LinkedList<File>();
		while(resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		LinkedList<Class<?>> classes = new LinkedList<Class<?>>();
		for(File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and sub
	 * directories.
	 * 
	 * @author Paulius Matulionis - stackoverflow.com
	 * @param directory
	 *            the base directory
	 * @param packageName
	 *            the package name for classes found inside the base directory
	 * @return the classes
	 * @throws ClassNotFoundException
	 *             if class not found exception occurrs
	 */
	private static LinkedList<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		LinkedList<Class<?>> classes = new LinkedList<Class<?>>();
		if(!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.isDirectory()) {
					classes.addAll(findClasses(file, packageName + "." + file.getName()));
				} else if(file.getName().endsWith(".class")) {
					classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				}
			}
		}
		return classes;
	}
}
