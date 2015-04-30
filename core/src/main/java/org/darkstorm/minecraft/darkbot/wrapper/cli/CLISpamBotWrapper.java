package org.darkstorm.minecraft.darkbot.wrapper.cli;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.Timer;

import joptsimple.*;

import org.darkstorm.minecraft.darkbot.*;
import org.darkstorm.minecraft.darkbot.ai.*;
import org.darkstorm.minecraft.darkbot.auth.*;
import org.darkstorm.minecraft.darkbot.event.EventHandler;
import org.darkstorm.minecraft.darkbot.event.general.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.ChatReceivedEvent;
import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;
import org.darkstorm.minecraft.darkbot.wrapper.backend.ChatBackend;
import org.darkstorm.minecraft.darkbot.wrapper.commands.*;

@SuppressWarnings("unused")
public class CLISpamBotWrapper extends MinecraftBotWrapper {
	private static final boolean createFaction = true;
	private static final List<CLISpamBotWrapper> bots = new ArrayList<CLISpamBotWrapper>();
	private static final char[] msgChars = new char[] { 'a', 'e', 'i', 'o', 'u' };
	private static final String[] spamList;
	private static ArrayList<String> captchaList = new ArrayList<String>();

	static {
		List<String> spamlist = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("spamlist.txt")));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty())
					continue;
				spamlist.add(line);
			}
			reader.close();
		} catch(Exception exception) {}
		spamList = spamlist.toArray(new String[0]);
	}

	private static String spamMessage = null;
	private static int messageDelay = 70;

	private ConnectionHandler connectionHandler;
	private Random random = new Random();
	private int nextSkill, nextBot, nextMsgChar, nextSpamList, tickDelay = 100, nextMessage;

	private CLISpamBotWrapper(MinecraftBot bot, String owner) {
		super(bot);
		synchronized(bots) {
			bots.add(this);
		}
		addOwner(owner);
		addBackend(new ChatBackend(this));

		TaskManager taskManager = bot.getTaskManager();
		taskManager.registerTask(new FollowTask(bot));
		taskManager.registerTask(new DefendTask(bot));
		taskManager.registerTask(new AttackTask(bot));
		taskManager.registerTask(new HostileTask(bot));
		taskManager.registerTask(new EatTask(bot));

		commandManager.register(new AttackAllCommand(this));
		commandManager.register(new AttackCommand(this));
		commandManager.register(new CalcCommand(this));
		commandManager.register(new ChatDelayCommand(this));
		commandManager.register(new DropAllCommand(this));
		commandManager.register(new DropCommand(this));
		commandManager.register(new DropIdCommand(this));
		commandManager.register(new EquipCommand(this));
		commandManager.register(new FollowCommand(this));
		commandManager.register(new InteractCommand(this));
		commandManager.register(new OwnerCommand(this));
		commandManager.register(new QuitCommand(this));
		commandManager.register(new SayCommand(this));
		commandManager.register(new SetWalkCommand(this));
		commandManager.register(new SpamCommand(this));
		commandManager.register(new StatusCommand(this));
		commandManager.register(new StopCommand(this));
		commandManager.register(new SwitchCommand(this));
		commandManager.register(new ToolCommand(this));
		commandManager.register(new WalkCommand(this));

		connectionHandler = bot.getConnectionHandler();
		Session session = bot.getSession();
		System.out.println("[" + session.getUsername() + "] Done! (" + bots.size() + ")");
	}

	@Override
	@EventHandler
	public void onChatReceived(ChatReceivedEvent event) {
		super.onChatReceived(event);
		String message = Util.stripColors(event.getMessage());
		if(message.startsWith("Please register with \"/register")) {
			String password = Util.generateRandomString(10 + random.nextInt(6));
			bot.say("/register " + password + " " + password);
		} else if(message.contains("You are not member of any faction.") && spamMessage != null && createFaction) {
			String msg = "/f create " + Util.generateRandomString(7 + random.nextInt(4));
			bot.say(msg);
		}
		for(String s : captchaList) {
			Matcher captchaMatcher = Pattern.compile(s).matcher(message);
			if(captchaMatcher.matches())
				bot.say(captchaMatcher.group(1));
		}
	}

	@Override
	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		synchronized(bots) {
			bots.remove(this);
		}
		super.onDisconnect(event);
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if(!bot.hasSpawned() || !bot.isConnected())
			return;
		if(tickDelay > 0) {
			tickDelay--;
			return;
		}

		MainPlayerEntity player = bot.getPlayer();
		if(player == null || !bot.hasSpawned() || spamMessage == null)
			return;
		if(nextMessage > 0) {
			nextMessage--;
			return;
		}
		try {
			String message = spamMessage;
			MessageFormatter formatter = new MessageFormatter();
			synchronized(bots) {
				if(bots.size() > 0) {
					CLISpamBotWrapper bot = bots.get(++nextBot >= bots.size() ? nextBot = 0 : nextBot);
					if(bot != null && bot.bot != null && bot.bot.getSession() != null)
						formatter.setVariable("bot", bot.bot.getSession().getUsername());
				}
			}
			if(spamList.length > 0) {
				formatter.setVariable("spamlist", spamList[++nextSpamList >= spamList.length ? nextSpamList = 0 : nextSpamList]);
			}
			formatter.setVariable("rnd", Util.generateRandomString(15 + random.nextInt(6)));
			formatter.setVariable("msg", Character.toString(msgChars[++nextMsgChar >= msgChars.length ? nextMsgChar = 0 : nextMsgChar]));
			message = formatter.format(message);
			bot.say(message);
		} catch(Exception e) {
			e.printStackTrace();
		}
		nextMessage = messageDelay;
	}

	public static String getSpamMessage() {
		return spamMessage;
	}

	public static void setSpamMessage(String spamMessage) {
		CLISpamBotWrapper.spamMessage = spamMessage;
	}

	public static void main(String[] args) {
		// TODO main
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("h", "help"), "Show this help dialog.");
		OptionSpec<String> serverOption = parser.acceptsAll(Arrays.asList("s", "server"), "Server to join.").withRequiredArg()
				.describedAs("server-address[:port]");
		OptionSpec<String> proxyOption = parser.acceptsAll(Arrays.asList("P", "proxy"), "SOCKS proxy to use. Ignored in presence of 'socks-proxy-list'.")
				.withRequiredArg().describedAs("proxy-address");
		OptionSpec<String> ownerOption = parser.acceptsAll(Arrays.asList("o", "owner"), "Owner of the bot (username of in-game control).").withRequiredArg()
				.describedAs("username");
		OptionSpec<?> offlineOption = parser.acceptsAll(Arrays.asList("O", "offline"), "Offline-mode. Ignores 'password' and 'account-list' (will "
				+ "generate random usernames if 'username' is not supplied).");
		OptionSpec<?> autoRejoinOption = parser.acceptsAll(Arrays.asList("a", "auto-rejoin"), "Auto-rejoin a server on disconnect.");
		OptionSpec<Integer> loginDelayOption = parser
				.acceptsAll(Arrays.asList("d", "login-delay"), "Delay between bot joins, in milliseconds. 5000 is " + "recommended if not using socks proxies.")
				.withRequiredArg().describedAs("delay").ofType(Integer.class);
		OptionSpec<Integer> botAmountOption = parser.acceptsAll(Arrays.asList("b", "bot-amount"), "Amount of bots to join. Must be <= amount of accounts.")
				.withRequiredArg().describedAs("amount").ofType(Integer.class);
		OptionSpec<String> protocolOption = parser.accepts("protocol", "Protocol version to use. Can be either protocol number or Minecraft version.")
				.withRequiredArg();
		OptionSpec<?> protocolsOption = parser.accepts("protocols", "List available protocols and exit.");

		OptionSpec<String> accountListOption = parser.accepts("account-list", "File containing a list of accounts, in username/email:password format.")
				.withRequiredArg().describedAs("file");
		OptionSpec<String> socksProxyListOption = parser.accepts("socks-proxy-list", "File containing a list of SOCKS proxies, in address:port format.")
				.withRequiredArg().describedAs("file");
		OptionSpec<String> httpProxyListOption = parser.accepts("http-proxy-list", "File containing a list of HTTP proxies, in address:port format.")
				.withRequiredArg().describedAs("file");
		OptionSpec<String> captchaListOption = parser.accepts("captcha-list", "File containing a list of chat baised captcha to bypass.").withRequiredArg()
				.describedAs("file");
		OptionSpec<?> torOption = parser.accepts("tor", "Use Tor rather than socks proxies to join");

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
			for(ProtocolProvider<?> provider : ProtocolProvider.getProviders())
				System.out.println("\t" + provider.getMinecraftVersion() + " (" + provider.getSupportedVersion() + "): " + provider.getClass().getName());
			System.out
					.println("If no protocols are listed above, you may attempt to specify a protocol version in case the provider is actually in the class-path.");
			return;
		}

		final boolean offline = options.has(offlineOption);
		final boolean autoRejoin = options.has(autoRejoinOption);

		final List<String> accounts;
		if(options.has(accountListOption)) {
			accounts = loadAccounts(options.valueOf(accountListOption));
		} else if(!offline) {
			System.out.println("Option 'accounts' must be supplied in " + "absence of option 'offline'.");
			printHelp(parser);
			return;
		} else
			accounts = null;

		final List<String> captcha;
		if(options.has(captchaListOption))
			readCaptchaFile(options.valueOf(captchaListOption));

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
				ProtocolProvider<?> foundProvider = null;
				for(ProtocolProvider<?> provider : ProtocolProvider.getProviders())
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
		final boolean useTor;
		if(options.has(torOption)) {
			useTor = true;
			socksProxies = null;
		} else {
			if(options.has(socksProxyListOption))
				socksProxies = loadProxies(options.valueOf(socksProxyListOption));
			else if(options.has(proxyOption))
				socksProxies = Arrays.asList(options.valueOf(proxyOption));
			else
				socksProxies = null;
			useTor = false;
		}

		final List<String> httpProxies;
		if(options.has(httpProxyListOption)) {
			httpProxies = loadLoginProxies(options.valueOf(httpProxyListOption));
			/*} else if(!offline && accounts != null) {
				System.out.println("Option 'http-proxy-list' required if " + "option 'account-list' is supplied.");
				printHelp(parser);
				return;*/
		} else
			httpProxies = null;

		final int loginDelay;
		if(options.has(loginDelayOption))
			loginDelay = options.valueOf(loginDelayOption);
		else
			loginDelay = 0;

		final int botAmount;
		if(!options.has(botAmountOption)) {
			System.out.println("Option 'bot-amount' required.");
			printHelp(parser);
			return;
		} else
			botAmount = options.valueOf(botAmountOption);

		initGui();
		while(!sessions.get()) {
			synchronized(sessions) {
				try {
					sessions.wait(5000);
				} catch(InterruptedException exception) {}
			}
		}

		final Queue<Object> lockQueue = new ArrayDeque<Object>();

		ExecutorService service = Executors.newCachedThreadPool();
		final Object firstWait = new Object();
		if(loginDelay > 0) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					try {
						while(!joins.get()) {
							synchronized(firstWait) {
								try {
									firstWait.wait();
								} catch(InterruptedException exception) {}
							}
						}
						while(true) {
							synchronized(lockQueue) {
								if(lockQueue.size() > 0) {
									Object thread = lockQueue.poll();
									synchronized(thread) {
										thread.notifyAll();
									}
								}
							}
							try {
								Thread.sleep(loginDelay);
							} catch(InterruptedException exception) {}
						}
					} catch(Throwable exception) {
						exception.printStackTrace();
					}
				}
			});
		}
		final List<String> accountsInUse = new ArrayList<String>();
		final Map<String, AtomicInteger> workingProxies = new HashMap<String, AtomicInteger>();
		for(int i = 0; i < botAmount; i++) {
			final int botNumber = i;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Random random = new Random();

					if(!offline) {
						AuthService<?> authService = new YggdrasilAuthService(MinecraftBot.CLIENT_TOKEN);
						boolean authenticated = false;
						user: while(true) {
							if(authenticated) {
								authenticated = false;
								sessionCount.decrementAndGet();
							}
							Session session = null;
							String loginProxy;
							String account = accounts.get(random.nextInt(accounts.size()));
							synchronized(accountsInUse) {
								if(accountsInUse.size() == accounts.size())
									System.exit(0);
								while(accountsInUse.contains(account))
									account = accounts.get(random.nextInt(accounts.size()));
								accountsInUse.add(account);
							}
							String[] accountParts = account.split(":");
							while(true) {
								while(!sessions.get()) {
									synchronized(sessions) {
										try {
											sessions.wait(5000);
										} catch(InterruptedException exception) {}
									}
								}
								synchronized(workingProxies) {
									Iterator<String> iterator = workingProxies.keySet().iterator();
									if(iterator.hasNext())
										loginProxy = iterator.next();
									else if(httpProxies != null)
										loginProxy = httpProxies.get(random.nextInt(httpProxies.size()));
									else
										loginProxy = null;
								}
								try {
									session = authService.login(accountParts[0], accountParts[1], toProxy(loginProxy, ProxyData.ProxyType.HTTP));
									// addAccount(session);
									synchronized(workingProxies) {
										AtomicInteger count = workingProxies.get(loginProxy);
										if(count != null)
											count.set(0);
										else
											workingProxies.put(loginProxy, new AtomicInteger());
									}
									sessionCount.incrementAndGet();
									authenticated = true;
									break;
								} catch(IOException exception) {
									synchronized(workingProxies) {
										workingProxies.remove(loginProxy);
									}
									System.err.println("[Bot" + botNumber + "] " + loginProxy + ": " + exception);
								} catch(AuthenticationException exception) {
									if(exception.getMessage().contains("Too many failed logins")) {
										synchronized(workingProxies) {
											AtomicInteger count = workingProxies.get(loginProxy);
											if(count != null && count.incrementAndGet() >= 5)
												workingProxies.remove(loginProxy);
										}
									}
									System.err.println("[Bot" + botNumber + "] " + loginProxy + ": " + exception);
									continue user;
								}
							}
							System.out.println("[" + session.getUsername() + "] " + session);
							while(!joins.get()) {
								synchronized(joins) {
									try {
										joins.wait(5000);
									} catch(InterruptedException exception) {}
								}
							}
							System.out.println("[" + session.getUsername() + "] Starting joins...");
							while(loginDelay > 0) {
								Object lock = new Object();
								synchronized(lockQueue) {
									lockQueue.add(lock);
								}
								synchronized(lock) {
									try {
										synchronized(firstWait) {
											firstWait.notifyAll();
										}
										lock.wait(5000);
										synchronized(lockQueue) {
											if(lockQueue.contains(lock))
												lockQueue.remove(lock);
											else
												break;
										}
									} catch(InterruptedException exception) {}
								}
							}

							while(true) {
								System.out.println("[" + session.getUsername() + "] 1");
								String proxy = socksProxies != null ? socksProxies.get(random.nextInt(socksProxies.size())) : null;
								try {
									CLISpamBotWrapper bot = new CLISpamBotWrapper(createBot(server,
																							session.getUsername(),
																							session.getPassword(),
																							authService,
																							session,
																							protocol,
																							null,
																							proxy,
																							useTor), owner);
									System.out.println("[" + session.getUsername() + "] 2");
									while(bot.getBot().isConnected()) {
										try {
											Thread.sleep(500);
										} catch(InterruptedException exception) {
											exception.printStackTrace();
										}
									}
									System.out.println("[" + session.getUsername() + "] 3");
									if(!autoRejoin)
										break;
								} catch(Exception exception) {
									exception.printStackTrace();
									System.out.println("[" + session.getUsername() + "] Error connecting: " + exception.getCause().toString());
								}
							}
							System.out.println("[" + session.getUsername() + "] Account failed");
						}
					} else {
						while(true) {
							String proxy = socksProxies != null ? socksProxies.get(random.nextInt(socksProxies.size())) : null;
							try {
								String username;
								if(accounts != null) {
									username = accounts.get(random.nextInt(accounts.size())).split(":")[0];
									synchronized(accountsInUse) {
										while(accountsInUse.contains(username))
											username = accounts.get(random.nextInt(accounts.size()));
										accountsInUse.add(username);
									}
								} else
									username = Util.generateRandomString(10 + random.nextInt(6));
								if(loginDelay > 0) {
									synchronized(this) {
										try {
											synchronized(firstWait) {
												firstWait.notifyAll();
											}
											wait();
										} catch(InterruptedException exception) {}
									}
								}
								CLISpamBotWrapper bot = new CLISpamBotWrapper(	createBot(server, username, null, null, null, protocol, null, proxy, useTor),
																				owner);
								while(bot.getBot().isConnected()) {
									try {
										Thread.sleep(500);
									} catch(InterruptedException exception) {
										exception.printStackTrace();
									}
								}
								if(!autoRejoin)
									break;
								else
									continue;
							} catch(Exception exception) {
								System.out.println("[Bot" + botNumber + "] Error connecting: " + exception.toString());
							}
						}
					}
				}

			};
			service.execute(runnable);
		}
		service.shutdown();
		while(!service.isTerminated()) {
			try {
				service.awaitTermination(9000, TimeUnit.DAYS);
			} catch(InterruptedException exception) {
				exception.printStackTrace();
			}
		}
		System.exit(0);
	}

	private static AtomicInteger sessionCount = new AtomicInteger();
	private static AtomicBoolean sessions = new AtomicBoolean();
	private static AtomicBoolean joins = new AtomicBoolean();

	private static void initGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		JFrame frame = new JFrame("DarkBot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		Insets noInsets = new Insets(0, 0, 0, 0);
		final JToggleButton sessionsButton = new JToggleButton("Login (0)");
		frame.add(sessionsButton, new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets, 0, 0));
		sessionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sessions.set(sessionsButton.isSelected());
				synchronized(sessions) {
					sessions.notifyAll();
				}
			}
		});
		final JToggleButton joinsButton = new JToggleButton("Join (0)");
		frame.add(joinsButton, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets, 0, 0));
		joinsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				joins.set(joinsButton.isSelected());
				synchronized(joins) {
					joins.notifyAll();
				}
			}
		});
		final JTextField field = new JTextField();
		frame.add(field, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets, 0, 0));
		final JButton button = new JButton("Start");
		frame.add(button, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets, 0, 0));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(button.getText().startsWith("Start")) {
					field.setEnabled(false);
					spamMessage = field.getText();
					button.setText("Stop");
				} else {
					spamMessage = null;
					button.setText("Start");
					field.setEnabled(true);
				}
			}
		});
		Timer timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sessionsButton.setText(sessionsButton.getText().split(" ")[0] + " (" + Integer.toString(sessionCount.get()) + ")");
				joinsButton.setText(joinsButton.getText().split(" ")[0] + " (" + Integer.toString(bots.size()) + ")");
			}
		});
		timer.setRepeats(true);
		timer.start();
		frame.pack();
		frame.setSize(500, frame.getHeight());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
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
			Pattern pattern = Pattern.compile("[a-zA-Z_0-9@\\.]{1,}");
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

	private static ProxyData toProxy(String proxyData, ProxyData.ProxyType type) {
		if(proxyData == null)
			return null;
		int port = 80;
		if(proxyData.contains(":")) {
			String[] parts = proxyData.split(":");
			proxyData = parts[0];
			port = Integer.parseInt(parts[1]);
		}
		return new ProxyData(proxyData, port, type);
	}

	private static MinecraftBot createBot(	String server,
											String username,
											String password,
											AuthService<?> service,
											Session session,
											int protocol,
											String loginProxy,
											String proxy,
											boolean tor) throws AuthenticationException, UnsupportedProtocolException, IOException {
		System.out.println("[" + session.getUsername() + "] create - " + server + " - " + password + " - " + protocol + " - " + loginProxy + " - " + proxy);
		MinecraftBot.Builder builder = MinecraftBot.builder();
		if(tor) {
			//builder.connectProxy(new ProxyData(null, 0, ProxyType.TOR));
		} else if(proxy != null && !proxy.isEmpty()) {
			int port = 80;
			ProxyType type = ProxyType.SOCKS;
			if(proxy.contains(":")) {
				String[] parts = proxy.split(":");
				proxy = parts[0];
				port = Integer.parseInt(parts[1]);
				if(parts.length > 2)
					type = ProxyType.values()[Integer.parseInt(parts[2]) - 1];
			}
			builder.connectProxy(new ProxyData(proxy, port, type));
		}
		if(loginProxy != null && !loginProxy.isEmpty()) {
			int port = 80;
			if(loginProxy.contains(":")) {
				String[] parts = loginProxy.split(":");
				loginProxy = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			builder.loginProxy(new ProxyData(loginProxy, port, ProxyType.HTTP));
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

	private static void readCaptchaFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			while((line = reader.readLine()) != null)
				captchaList.add(line);
			reader.close();
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final class MessageFormatter {
		private static final Pattern variablePattern = Pattern.compile("(?i)\\$(\\{[a-z0-9_]{1,}\\}|[a-z0-9_]{1,})");
		private static final Pattern colorPattern = Pattern.compile("(?i)&[0-9A-FK-OR]");

		private final Map<String, String> variables;

		public MessageFormatter() {
			variables = new HashMap<String, String>();
		}

		public synchronized void setVariable(String variable, String value) {
			if(value == null)
				variables.remove(value);
			else
				variables.put(variable, value);
		}

		public synchronized String format(String message) {
			Matcher matcher = variablePattern.matcher(message);
			while(matcher.find()) {
				String variable = matcher.group();
				variable = variable.substring(1);
				if(variable.startsWith("{") && variable.endsWith("}"))
					variable = variable.substring(1, variable.length() - 1);
				String value = variables.get(variable);
				if(value == null)
					value = "";
				message = message.replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(value));
			}
			matcher = colorPattern.matcher(message);
			while(matcher.find())
				message = message.substring(0, matcher.start()) + "\247" + message.substring(matcher.end() - 1);
			return message;
		}
	}
}
