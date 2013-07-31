package org.darkstorm.darkbot.minecraftbot.util;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.*;

import javax.net.ssl.HttpsURLConnection;
import javax.script.*;

import org.darkstorm.darkbot.minecraftbot.auth.Session;

public final class Util {
	private static final ScriptEngine engine;
	private static final Random random = new Random();

	static {
		ScriptEngineManager mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("JavaScript");
	}

	private Util() {
		throw new UnsupportedOperationException();
	}

	public static Object eval(String javascript) {
		try {
			return engine.eval(javascript);
		} catch(ScriptException exception) {
			return null;
		}
	}

	public static String post(String targetURL, String urlParameters, Proxy proxy) throws IOException {
		HttpsURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			if(proxy != null)
				connection = (HttpsURLConnection) url.openConnection(proxy);
			else
				connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(3000);

			connection.connect();

			Certificate[] certs = connection.getServerCertificates();

			byte[] bytes = new byte[294];
			DataInputStream dis = new DataInputStream(Session.class.getResourceAsStream("/minecraft.key"));
			dis.readFully(bytes);
			dis.close();

			Certificate c = certs[0];
			PublicKey pk = c.getPublicKey();
			byte[] data = pk.getEncoded();

			for(int i = 0; i < data.length; i++)
				if(data[i] != bytes[i])
					throw new RuntimeException("Public key mismatch");

			try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
				out.writeBytes(urlParameters);
				out.flush();
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				StringBuffer response = new StringBuffer();
				String line;
				while((line = reader.readLine()) != null)
					response.append(line).append('\r');
				return response.toString();
			}
		} finally {
			if(connection != null)
				connection.disconnect();
		}
	}

	public static String stripColors(final String input) {
		if(input != null)
			return input.replaceAll("(?i)§[0-9a-fk-or]", "");
		return null;
	}

	public static String join(Object[] objects) {
		return join(objects, ", ", "");
	}

	public static String join(Object[] objects, String separator) {
		return join(objects, separator, "");
	}

	public static String join(Object[] objects, String separator, String finalSeparator) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < objects.length; i++) {
			if(i != 0)
				builder.append(separator);
			if(i == objects.length - 1)
				builder.append(finalSeparator);
			builder.append(objects[i]);
		}
		return builder.toString();
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

	public static String generateRandomString(int length) {
		char[] characters = new char[length];
		for(int i = 0; i < length; i++) {
			char start, end;
			switch(random.nextInt(4)) {
			case 0:
				start = 'A';
				end = 'Z';
			case 1:
				start = '0';
				end = '9';
			default:
				start = 'a';
				end = 'z';
			}
			characters[i] = (char) (start + random.nextInt(end - start));
		}
		return new String(characters);
	}
}
