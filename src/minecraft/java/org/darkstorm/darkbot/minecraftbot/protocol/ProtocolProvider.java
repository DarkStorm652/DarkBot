package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.File;
import java.net.*;
import java.util.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;

public abstract class ProtocolProvider {
	private static final List<ProtocolProvider> providers = loadProviders();

	private static final List<ProtocolProvider> loadProviders() {
		List<ProtocolProvider> providers = new ArrayList<>();

		URL[] urls = locateProtocolJars();
		URLClassLoader classLoader = new URLClassLoader(urls);
		ServiceLoader<ProtocolProvider> providerLoader = ServiceLoader.load(ProtocolProvider.class, classLoader);
		loop: for(ProtocolProvider provider : providerLoader) {
			for(ProtocolProvider installed : providers)
				if(provider.getSupportedVersion() == installed.getSupportedVersion())
					continue loop;
			providers.add(provider);
		}

		return Collections.unmodifiableList(providers);
	}

	private static final URL[] locateProtocolJars() {
		File protocolsDirectory = new File("protocols");
		File[] files = protocolsDirectory.listFiles();
		if(files == null || files.length == 0)
			return new URL[0];
		List<URL> urls = new ArrayList<>();
		for(File file : files)
			if(file.getName().endsWith(".jar"))
				try {
					urls.add(file.toURI().toURL());
				} catch(MalformedURLException exception) {}
		return urls.toArray(new URL[urls.size()]);
	}

	public static final List<ProtocolProvider> getProviders() {
		return providers;
	}

	public static final ProtocolProvider getProvider(int version) {
		for(ProtocolProvider provider : providers)
			if(version == provider.getSupportedVersion())
				return provider;
		try {
			String className = "org.darkstorm.darkbot.minecraftbot.protocol.v" + version + ".Protocol" + version + "$Provider";
			Class<? extends ProtocolProvider> providerClass = Class.forName(className).asSubclass(ProtocolProvider.class);
			return providerClass.newInstance();
		} catch(Throwable exception) {
			return null;
		}
	}

	public static final ProtocolProvider getLatestProvider() {
		ProtocolProvider latestProvider = null;
		for(ProtocolProvider provider : providers)
			if(latestProvider == null || provider.getSupportedVersion() > latestProvider.getSupportedVersion())
				latestProvider = provider;
		return latestProvider;
	}

	public abstract Protocol getProtocolInstance(MinecraftBot bot);

	public abstract int getSupportedVersion();

	public abstract String getMinecraftVersion();
}
