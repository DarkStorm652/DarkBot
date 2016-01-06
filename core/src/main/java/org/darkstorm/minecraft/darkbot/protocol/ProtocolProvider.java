package org.darkstorm.minecraft.darkbot.protocol;

import java.io.File;
import java.net.*;
import java.util.*;

import org.darkstorm.minecraft.darkbot.MinecraftBot;

public abstract class ProtocolProvider<T extends Protocol<?>> {
	private static final List<ProtocolProvider<?>> providers = loadProviders();

	private static final List<ProtocolProvider<?>> loadProviders() {
		List<ProtocolProvider<?>> providers = new ArrayList<>();

		URL[] urls = locateProtocolJars();
		URLClassLoader classLoader = new URLClassLoader(urls);
		@SuppressWarnings("rawtypes")
		ServiceLoader<ProtocolProvider> providerLoader = ServiceLoader.load(ProtocolProvider.class, classLoader);
		loop: for(ProtocolProvider<?> provider : providerLoader) {
			for(ProtocolProvider<?> installed : providers)
				if(provider.getSupportedVersion() == installed.getSupportedVersion() && (provider instanceof ProtocolProviderX == installed instanceof ProtocolProviderX))
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

	public static final List<ProtocolProvider<?>> getProviders() {
		return providers;
	}

	public static final ProtocolProvider<?> getProvider(int version) {
		ProtocolProvider<?> provider = getProvider(version, true);
		if(provider != null)
			return provider;
		return getProvider(version, false);
	}

	public static final ProtocolProvider<?> getProvider(int version, boolean x) {
		for(ProtocolProvider<?> provider : providers)
			if(version == provider.getSupportedVersion() && (x == provider instanceof ProtocolProviderX))
				return provider;
		try {
			String className = ProtocolProvider.class.getPackage().getName() + ".v" + version + (x ? "x" : "") + ".Protocol" + version + (x ? "X" : "") + "$Provider";
			@SuppressWarnings("rawtypes")
			Class<? extends ProtocolProvider> providerClass = Class.forName(className).asSubclass(ProtocolProvider.class);
			return providerClass.newInstance();
		} catch(Throwable exception) {
			return null;
		}
	}

	public static final ProtocolProvider<?> getLatestProvider() {
		ProtocolProvider<?> provider = getLatestProvider(true);
		if(provider != null)
			return provider;
		return getLatestProvider(false);
	}

	public static final ProtocolProvider<?> getLatestProvider(boolean x) {
		ProtocolProvider<?> latestProvider = null;
		for(ProtocolProvider<?> provider : providers)
			if((x == provider instanceof ProtocolProviderX) && (latestProvider == null || provider.getSupportedVersion() > latestProvider.getSupportedVersion()))
				latestProvider = provider;
		return latestProvider;
	}

	public abstract T getProtocolInstance(MinecraftBot bot);

	public abstract int getSupportedVersion();

	public abstract String getMinecraftVersion();
}
