package org.darkstorm.minecraft.darkbot.wrapper.cli;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import joptsimple.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.darkstorm.minecraft.darkbot.*;
import org.darkstorm.minecraft.darkbot.ai.*;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;
import org.darkstorm.minecraft.darkbot.wrapper.backend.ChatBackend;
import org.darkstorm.minecraft.darkbot.wrapper.commands.*;

public class CLIBotWrapper extends MinecraftBotWrapper {
	private CLIBotWrapper(MinecraftBotImpl bot, String owner) {
		super(bot);
		addOwner(owner);
		addBackend(new ChatBackend(this));

		TaskManager taskManager = bot.getTaskManager();
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
		taskManager.registerTask(new DerpTask(bot));
		taskManager.registerTask(new TwerkTask(bot));
		taskManager.registerTask(new MirrorTask(bot));

		taskManager.registerTask(new XRayMiningTask(bot));

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
		commandManager.register(new DerpCommand(this));
		commandManager.register(new TwerkCommand(this));
		commandManager.register(new CrouchCommand(this));
		commandManager.register(new MirrorCommand(this));
		commandManager.register(new MuteCommand(this));

		commandManager.register(new LoginCommand(this));
		commandManager.register(new XRayMineCommand(this));
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
		OptionSpec<?> mcoServersOption = parser.accepts("mco-servers", "List available MCO (Realms) servers.");
		OptionSpec<String> mcoServerOption = parser.accepts("mco-server", "Connect to an MCO (Realms) server. Can be a name, ID, or index (in order of priority).").withRequiredArg().describedAs("server");

		OptionSpec<String> accountListOption = parser.accepts("account-list", "File containing a list of accounts, in username/email:password format.").withRequiredArg().describedAs("file");
		OptionSpec<String> socksProxyListOption = parser.accepts("socks-proxy-list", "File containing a list of SOCKS proxies, in address:port format.").withRequiredArg().describedAs("file");
		OptionSpec<String> httpProxyListOption = parser.accepts("http-proxy-list", "File containing a list of HTTP proxies, in address:port format.").withRequiredArg().describedAs("file");

		OptionSet options = CLIWrapperUtils.parseOptions(parser, args);

		if(options.has("help")) {
            CLIWrapperUtils.printHelp(parser);
			return;
		}
		if(options.has(protocolsOption)) {
			//TODO: Implement
			System.out.println("Not implemented!");
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
					return;
				} else
					password = null;
			} else {
				username = null;
				password = null;
			}
		}

		if(options.has(mcoServersOption)) {
			//TODO: Implement
			System.out.println("Not implemented!");
			return;
		}

		if(!options.has(serverOption)) {
			System.out.println("Option 'server' required.");
			return;
		}

		final String server = options.valueOf(serverOption);
		final String owner = CLIWrapperUtils.getRequiredOption(options, ownerOption);

		final List<String> accountsInUse = new ArrayList<String>();
		Random random = new Random();

		if(!offline) {
			//TODO: Implement
			System.out.println("Not implemented!");
		} else {
			while(true) {
				try {
					String name = "";
					if(username == null)
						name = RandomStringUtils.randomAlphanumeric(10 + random.nextInt(6));
					else
						name = username;
					CLIBotWrapper bot = new CLIBotWrapper(createBot(server, name), owner);
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
					System.out.println("[Bot] Error connecting: " + exception.toString());
					exception.printStackTrace();
				}
			}
		}
		System.exit(0);
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

	private static MinecraftBotImpl createBot(String server, String username) throws IOException {
		MinecraftBotImpl.Builder builder = MinecraftBotImpl.builder();
		builder.username(username);

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