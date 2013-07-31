package org.darkstorm.darkbot.mcspambot;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import joptsimple.*;

import org.darkstorm.darkbot.mcspambot.commands.*;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.auth.*;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.util.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData.ProxyType;

public class DarkBotMC extends MinecraftBotWrapper {
	private DarkBotMC(MinecraftBotData data, String owner) throws AuthenticationException, UnsupportedProtocolException {
		super(data);
		addOwner(owner);
		addBackend(new ChatBackend(this));

		TaskManager taskManager = bot.getTaskManager();
		taskManager.registerTask(new FallTask(bot));
		taskManager.registerTask(new ChopTreesTask(bot));
		taskManager.registerTask(new FollowTask(bot));
		taskManager.registerTask(new DefendTask(bot));
		taskManager.registerTask(new AttackTask(bot));
		taskManager.registerTask(new HostileTask(bot));
		taskManager.registerTask(new EatTask(bot));
		taskManager.registerTask(new MiningTask(bot));
		taskManager.registerTask(new FishingTask(bot));
		taskManager.registerTask(new FarmingTask(bot));
		taskManager.registerTask(new BuildingTask(bot));
		taskManager.registerTask(new AvoidDeathTask(bot));
		taskManager.registerTask(new DestroyingTask(bot));

		commandManager.register(new AttackAllCommand(this));
		commandManager.register(new AttackCommand(this));
		commandManager.register(new BuildCommand(this));
		commandManager.register(new CalcCommand(this));
		commandManager.register(new ChatDelayCommand(this));
		commandManager.register(new ChopCommand(this));
		commandManager.register(new DestroyCommand(this));
		commandManager.register(new DropAllCommand(this));
		commandManager.register(new DropCommand(this));
		commandManager.register(new DropIdCommand(this));
		commandManager.register(new EquipCommand(this));
		commandManager.register(new FarmCommand(this));
		commandManager.register(new FishCommand(this));
		commandManager.register(new FollowCommand(this));
		commandManager.register(new HelpCommand(this));
		commandManager.register(new InteractCommand(this));
		commandManager.register(new MineCommand(this));
		commandManager.register(new OwnerCommand(this));
		commandManager.register(new PlayersCommand(this));
		commandManager.register(new QuitCommand(this));
		commandManager.register(new SayCommand(this));
		commandManager.register(new SetWalkCommand(this));
		commandManager.register(new StatusCommand(this));
		commandManager.register(new StopCommand(this));
		commandManager.register(new SwitchCommand(this));
		commandManager.register(new ToolCommand(this));
		commandManager.register(new WalkCommand(this));
	}

	public static void main(String[] args) {
		// TODO main
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("h", "help"), "Show this help dialog.");
		OptionSpec<String> serverOption = parser.acceptsAll(Arrays.asList("s", "server"), "Server to join.").withRequiredArg().describedAs("server-address[:port]");
		OptionSpec<String> proxyOption = parser.acceptsAll(Arrays.asList("P", "proxy"), "SOCKS proxy to use. Ignored in presence of 'socks-proxy-list'.").withRequiredArg().describedAs("proxy-address");
		OptionSpec<String> ownerOption = parser.acceptsAll(Arrays.asList("o", "owner"), "Owner of the bot (username of in-game control).").withRequiredArg().describedAs("username");
		OptionSpec<String> usernameOption = parser.acceptsAll(Arrays.asList("u", "username"), "Bot username. Ignored in presence of 'account-list'.").withRequiredArg().describedAs("username/email");
		OptionSpec<String> passwordOption = parser.acceptsAll(Arrays.asList("p", "password"), "Bot password. Ignored in presence of 'offline' or " + "'account-list', or if 'username' is not supplied.").withRequiredArg().describedAs("password");
		OptionSpec<?> offlineOption = parser.acceptsAll(Arrays.asList("O", "offline"), "Offline-mode. Ignores 'password' and 'account-list' (will " + "generate random usernames if 'username' is not supplied).");
		OptionSpec<?> autoRejoinOption = parser.acceptsAll(Arrays.asList("a", "auto-rejoin"), "Auto-rejoin a server on disconnect.");
		OptionSpec<String> protocolOption = parser.accepts("protocol", "Protocol version to use. Can be either protocol number or Minecraft version.").withRequiredArg();
		OptionSpec<?> protocolsOption = parser.accepts("protocols", "List available protocols and exit.");

		OptionSpec<String> accountListOption = parser.accepts("account-list", "File containing a list of accounts, in username/email:password format.").withRequiredArg().describedAs("file");
		OptionSpec<String> socksProxyListOption = parser.accepts("socks-proxy-list", "File containing a list of SOCKS proxies, in address:port format.").withRequiredArg().describedAs("file");
		OptionSpec<String> httpProxyListOption = parser.accepts("http-proxy-list", "File containing a list of HTTP proxies, in address:port format.").withRequiredArg().describedAs("file");

		OptionSet options;
		try {
			options = parser.parse(args);
		} catch(OptionException exception) {
			try {
				parser.printHelpOn(System.out);
			} catch(Exception exception1) {
				exception1.printStackTrace();
			}
			return;
		}

		if(options.has("help")) {
			printHelp(parser);
			return;
		}
		if(options.has(protocolsOption)) {
			System.out.println("Available protocols:");
			for(ProtocolProvider provider : ProtocolProvider.getProviders())
				System.out.println("\t" + provider.getMinecraftVersion() + " (" + provider.getSupportedVersion() + "): " + provider.getClass().getName());
			System.out.println("If no protocols are listed above, you may attempt to specify a protocol version in case the provider is actually in the class-path.");
			System.out.println("If no protocol is specified, it wil take the latest protocol version, assuming there are protocols listed above.");
			return;
		}

		final boolean offline = options.has(offlineOption);
		final boolean autoRejoin = options.has(autoRejoinOption);

		final List<String> accounts;
		final String username, password;
		if(options.has(accountListOption)) {
			accounts = loadAccounts(options.valueOf(accountListOption));
			username = null;
			password = null;
		} else {
			accounts = null;
			if(options.has(usernameOption)) {
				username = options.valueOf(usernameOption);
				if(!offline && options.has(passwordOption))
					password = options.valueOf(passwordOption);
				else if(!offline) {
					System.out.println("Option 'password' or option " + "'offline' required.");
					printHelp(parser);
					return;
				} else
					password = null;
			} else {
				username = null;
				password = null;
			}
		}

		final String server;
		if(!options.has(serverOption)) {
			System.out.println("Option 'server' required.");
			printHelp(parser);
			return;
		} else
			server = options.valueOf(serverOption);

		final String owner;
		if(!options.has(ownerOption)) {
			System.out.println("Option 'owner' required.");
			printHelp(parser);
			return;
		} else
			owner = options.valueOf(ownerOption);

		final int protocol;
		if(options.has(protocolOption)) {
			String protocolString = options.valueOf(protocolOption);
			int parsedProtocol;
			try {
				parsedProtocol = Integer.parseInt(protocolString);
			} catch(NumberFormatException exception) {
				ProtocolProvider foundProvider = null;
				for(ProtocolProvider provider : ProtocolProvider.getProviders())
					if(protocolString.equals(provider.getMinecraftVersion()))
						foundProvider = provider;
				if(foundProvider == null) {
					System.out.println("No provider found for Minecraft version '" + protocolString + "'.");
					return;
				} else
					parsedProtocol = foundProvider.getSupportedVersion();
			}
			protocol = parsedProtocol;
		} else
			protocol = MinecraftBot.LATEST_PROTOCOL;

		final List<String> socksProxies;
		final String defaultProxy;
		if(options.has(socksProxyListOption)) {
			socksProxies = loadProxies(options.valueOf(socksProxyListOption));
			defaultProxy = null;
		} else {
			socksProxies = null;
			if(options.has(proxyOption))
				defaultProxy = options.valueOf(proxyOption);
			else
				defaultProxy = null;
		}
		final boolean useProxy = defaultProxy != null || socksProxies != null;

		final List<String> httpProxies;
		if(options.has(httpProxyListOption))
			httpProxies = loadLoginProxies(options.valueOf(httpProxyListOption));
		else if(username == null && accounts != null) {
			System.out.println("Option 'http-proxy-list' required in presence " + "of option 'account-list'.");
			printHelp(parser);
			return;
		} else
			httpProxies = null;

		final List<String> accountsInUse = new ArrayList<String>();
		Random random = new Random();

		if(!offline) {
			AuthService authService = new LegacyAuthService();
			user: do {
				Session session = null;
				String loginProxy;
				String account;
				if(username == null) {
					account = accounts.get(random.nextInt(accounts.size()));
					synchronized(accountsInUse) {
						while(accountsInUse.contains(account))
							account = accounts.get(random.nextInt(accounts.size()));
						accountsInUse.add(account);
					}
				} else
					account = username + ":" + password;
				String[] accountParts = account.split(":");
				while(true) {
					loginProxy = username != null ? null : httpProxies.get(random.nextInt(httpProxies.size()));
					try {
						session = authService.login(accountParts[0], accountParts[1], toProxy(loginProxy, Proxy.Type.HTTP));
						break;
					} catch(IOException exception) {
						System.err.println("[Bot] " + exception);
						if(username != null)
							break user;
					} catch(AuthenticationException exception) {
						System.err.println("[Bot] " + exception);
						if(username != null)
							break user;
						continue user;
					}
				}
				System.out.println("[" + session.getUsername() + "] " + session);

				while(true) {
					String proxy = useProxy ? defaultProxy != null ? defaultProxy : socksProxies.get(random.nextInt(socksProxies.size())) : null;
					try {
						DarkBotMC bot = new DarkBotMC(generateData(server, session.getUsername(), session.getPassword(), authService, session, protocol, null, proxy), owner);
						if(!bot.getBot().isConnected())
							System.out.println("[" + session.getUsername() + "] Account failed");
						while(bot.getBot().isConnected()) {
							try {
								Thread.sleep(1500);
							} catch(InterruptedException exception) {
								exception.printStackTrace();
							}
						}
						if(!autoRejoin)
							break;
					} catch(Exception exception) {
						exception.printStackTrace();
						System.out.println("[" + session.getUsername() + "] Error connecting: " + exception.getCause().toString());
					}
				}
			} while(username == null);
		} else {
			while(true) {
				String proxy = useProxy ? socksProxies.get(random.nextInt(socksProxies.size())) : null;
				try {
					String name = "";
					if(username == null)
						name = Util.generateRandomString(10 + random.nextInt(6));
					else
						name = username;
					DarkBotMC bot = new DarkBotMC(generateData(server, name, null, null, null, protocol, null, proxy), owner);
					while(bot.getBot().isConnected()) {
						try {
							Thread.sleep(1500);
						} catch(InterruptedException exception) {
							exception.printStackTrace();
						}
					}
					if(!autoRejoin)
						break;
					else
						continue;
				} catch(Exception exception) {
					System.out.println("[Bot] Error connecting: " + exception.toString());
					exception.printStackTrace();
				}
			}
		}
		System.exit(0);
	}

	private static void printHelp(OptionParser parser) {
		try {
			parser.printHelpOn(System.out);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private static List<String> loadProxies(String fileName) {
		List<String> proxies = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty())
					continue;
				String[] parts = line.split(" ")[0].trim().split(":");
				proxies.add(parts[0].trim() + ":" + Integer.parseInt(parts[1].trim()));
			}
			reader.close();
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		System.out.println("Loaded " + proxies.size() + " proxies.");
		return proxies;
	}

	private static List<String> loadLoginProxies(String fileName) {
		List<String> proxies = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty())
					continue;
				String[] parts = line.split(" ")[0].trim().split(":");
				proxies.add(parts[0].trim() + ":" + Integer.parseInt(parts[1].trim()));
			}
			reader.close();
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		System.out.println("Loaded " + proxies.size() + " login proxies.");
		return proxies;
	}

	private static List<String> loadAccounts(String fileName) {
		List<String> accounts = new ArrayList<String>();
		try {
			Pattern pattern = Pattern.compile("[\\w]{1,16}");
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			while((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if(!matcher.find())
					continue;
				String username = matcher.group();
				if(!matcher.find())
					continue;
				String password = matcher.group();
				accounts.add(username + ":" + password);
			}
			reader.close();
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		System.out.println("Loaded " + accounts.size() + " accounts.");
		return accounts;
	}

	private static Proxy toProxy(String proxyData, Proxy.Type type) {
		if(proxyData == null)
			return null;
		int port = 80;
		if(proxyData.contains(":")) {
			String[] parts = proxyData.split(":");
			proxyData = parts[0];
			port = Integer.parseInt(parts[1]);
		}
		return new Proxy(type, new InetSocketAddress(proxyData, port));
	}

	private static MinecraftBotData generateData(String server, String username, String password, AuthService service, Session session, int protocol, String loginProxy, String proxy) {
		MinecraftBotData.Builder builder = MinecraftBotData.builder();
		if(proxy != null && !proxy.isEmpty()) {
			int port = 80;
			ProxyType type = ProxyType.SOCKS;
			if(proxy.contains(":")) {
				String[] parts = proxy.split(":");
				proxy = parts[0];
				port = Integer.parseInt(parts[1]);
				if(parts.length > 2)
					type = ProxyType.values()[Integer.parseInt(parts[2]) - 1];
			}
			builder.socksProxy(new ProxyData(proxy, port, type));
		}
		if(loginProxy != null && !loginProxy.isEmpty()) {
			int port = 80;
			if(loginProxy.contains(":")) {
				String[] parts = loginProxy.split(":");
				loginProxy = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			builder.httpProxy(new ProxyData(loginProxy, port, ProxyType.HTTP));
		}
		builder.username(username).authService(service).protocol(protocol);
		if(session != null)
			builder.session(session);
		else
			builder.password(password);
		if(server != null && !server.isEmpty()) {
			int port = 25565;
			if(server.contains(":")) {
				String[] parts = server.split(":");
				server = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			builder.server(server).port(port);
		} else
			throw new IllegalArgumentException("Unknown server!");

		return builder.build();
	}
}
