package org.darkstorm.minecraft.darkbot.protocol;

import java.io.File;
import java.net.*;
import java.util.*;


public abstract class ProtocolProvider {
	private static final Map<String, Set<ProtocolProvider>> providersByVersion;
	private static final List<ProtocolProvider> providers;
	
	static {
		providers = loadProviders();
		providersByVersion = filterProviders(providers);
	}

	private static final List<ProtocolProvider> loadProviders() {
		List<ProtocolProvider> providers = new ArrayList<>();

		URL[] urls = locateProtocolJars();
		URLClassLoader classLoader = new URLClassLoader(urls);
		ServiceLoader<ProtocolProvider> providerLoader = ServiceLoader.load(ProtocolProvider.class, classLoader);
		for(ProtocolProvider provider : providerLoader)
			providers.add(provider);

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
	
	private static final Map<String, Set<ProtocolProvider>> filterProviders(List<ProtocolProvider> providers) {
		Map<String, Set<ProtocolProvider>> mapped = new HashMap<>();
		
		for(ProtocolProvider provider : providers) {
			for(String version : provider.getSupportedVersionNames()) {
				Set<ProtocolProvider> set = mapped.get(version);
				
				if(set == null)
					mapped.put(version, set = new HashSet<>());
				
				set.add(provider);
			}
		}
		
		for(String version : mapped.keySet())
			mapped.put(version, Collections.unmodifiableSet(mapped.get(version)));
		
		return Collections.unmodifiableMap(mapped);
	}

	public static final Collection<ProtocolProvider> getProviders() {
		return providers;
	}

	public static final Collection<ProtocolProvider> getProviders(String version) {
		Collection<ProtocolProvider> providers = providersByVersion.get(version);
		if(providers == null)
			return Collections.emptySet();
		return providers;
	}
	
	public static final Collection<String> getAllSupportedVersionNames() {
		return providersByVersion.keySet();
	}
	
	public abstract Collection<String> getSupportedVersionNames();
	public abstract Protocol createProtocolInstance();
}