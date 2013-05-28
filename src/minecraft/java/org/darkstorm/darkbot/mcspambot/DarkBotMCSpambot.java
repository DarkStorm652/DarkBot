package org.darkstorm.darkbot.mcspambot;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;

import javax.naming.AuthenticationException;
import javax.script.*;
import javax.swing.*;
import javax.swing.Timer;

import joptsimple.*;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.*;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.events.world.SpawnEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.Packet8UpdateHealth;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet205ClientCommand;
import org.darkstorm.darkbot.minecraftbot.util.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData.ProxyType;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

@SuppressWarnings("unused")
public class DarkBotMCSpambot implements EventListener {
	public static final DarkBot DARK_BOT = new DarkBot();

	private static final AtomicInteger amountJoined = new AtomicInteger();
	private static final boolean createFaction = true;
	private static final List<DarkBotMCSpambot> bots = new ArrayList<DarkBotMCSpambot>();
	private static final char[] msgChars = new char[] { 'a', 'e', 'i', 'o', 'u' };

	private static final String[] spamList;

	private static AtomicInteger slotsTaken = new AtomicInteger();

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

	private MinecraftBot bot;
	private Proxy proxy;
	private Proxy loginProxy;
	private ConnectionHandler connectionHandler;
	private Random random = new Random();

	private int nextSkill = 0, nextBot = 0, nextMsgChar = 0, nextSpamList = 0;

	private static String spamMessage = null;
	private static boolean die = false;
	private static int messageDelay = 70;

	private String owner;

	private boolean firstStart = false;
	private int ticksToGo = 200, nextMessage = 0;
	private boolean canSpam = false;

	private DarkBotMCSpambot(DarkBot darkBot, String server, String username, String password, String sessionId, String loginProxy, String proxy, String owner) {
		synchronized(bots) {
			bots.add(this);
			// slotsTaken.incrementAndGet();
			synchronized(slotsTaken) {
				slotsTaken.notifyAll();
			}
		}
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
			builder.withSocksProxy(new ProxyData(proxy, port, type));
			this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy, port));
		}
		if(loginProxy != null && !loginProxy.isEmpty()) {
			int port = 80;
			if(loginProxy.contains(":")) {
				String[] parts = loginProxy.split(":");
				loginProxy = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			builder.withHttpProxy(new ProxyData(loginProxy, port, ProxyType.HTTP));
			this.loginProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(loginProxy, port));
		}
		builder.withUsername(username);
		if(sessionId != null)
			builder.withSessionId(sessionId);
		else
			builder.withPassword(password);
		if(server != null && !server.isEmpty()) {
			int port = 25565;
			if(server.contains(":")) {
				String[] parts = server.split(":");
				server = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			builder.withServer(server).withPort(port);
		} else
			throw new IllegalArgumentException("Unknown server!");

		this.owner = owner;
		MinecraftBotData botData = builder.build();
		System.setProperty("socksProxyHost", "");
		System.setProperty("socksProxyPort", "");
		System.out.println("[" + username + "] Connecting...");
		bot = new MinecraftBot(darkBot, botData);
		TaskManager taskManager = bot.getTaskManager();
		taskManager.registerTask(new FallTask(bot));
		taskManager.registerTask(new FollowTask(bot));
		taskManager.registerTask(new DefendTask(bot));
		taskManager.registerTask(new AttackTask(bot));
		taskManager.registerTask(new HostileTask(bot));
		taskManager.registerTask(new EatTask(bot));
		// bot.setMovementDisabled(true);
		connectionHandler = bot.getConnectionHandler();
		Session session = bot.getSession();
		System.gc();
		System.out.println("[" + username + "] Done! (" + amountJoined.incrementAndGet() + ")");
		bot.getEventManager().registerListener(this);

		long lastShoutTime = System.currentTimeMillis();
		while(bot.isConnected()) {

		}
		synchronized(bots) {
			bots.remove(this);
		}
		amountJoined.decrementAndGet();
		slotsTaken.decrementAndGet();
		synchronized(slotsTaken) {
			slotsTaken.notifyAll();
		}
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		// System.out.println("Packet received: " + event.getPacket().getId()
		// + " (" + event.getPacket() + ")");
		Packet packet = event.getPacket();
		switch(packet.getId()) {
		case 0:
			connectionHandler.sendPacket(new Packet0KeepAlive(new Random().nextInt()));
			break;
		case 3:
			String message = ((Packet3Chat) packet).message;
			message = Util.stripColors(message);
			System.out.println("[" + bot.getSession().getUsername() + "] " + message);
			String testMessage = "[MineCaptcha] To be unmuted answer this question: What is ";
			String testMessage2 = "Please type '";
			String testMessage3 = "' to continue sending messages/commands";
			if(message.contains(testMessage)) {
				try {
					String captcha = message.split(Pattern.quote(testMessage))[1].split("[ \\?]")[0];
					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine engine = mgr.getEngineByName("JavaScript");
					String solved = engine.eval(captcha).toString();
					solved = solved.split("\\.")[0];
					connectionHandler.sendPacket(new Packet3Chat(solved));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} else if(message.contains(testMessage2) && message.contains(testMessage3)) {
				try {
					String captcha = message.split(Pattern.quote(testMessage2))[1].split(Pattern.quote(testMessage3))[0];
					connectionHandler.sendPacket(new Packet3Chat(captcha));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} else if(message.startsWith("Please register with \"/register")) {
				String password = Util.generateRandomString(10 + random.nextInt(6));
				bot.say("/register " + password + " " + password);
			} else if(message.startsWith("/uc ")) {
				connectionHandler.sendPacket(new Packet3Chat(message));
			} else if((message.contains("do the crime") && message.contains("do the time")) || message.contains("You have been muted")) {
				connectionHandler.sendPacket(new Packet3Chat("\247Leaving!"));
			} else if(message.contains(owner + " has requested to teleport to you.")) {
				connectionHandler.sendPacket(new Packet3Chat("/tpaccept"));
			} else if(message.contains(owner)) {
				if(message.contains("Go ")) {
					spamMessage = message.substring(message.indexOf("Go ") + "Go ".length());
				} else if(message.contains("Stop")) {
					spamMessage = null;
					bot.getTaskManager().stopAll();
					bot.setActivity(null);
				} else if(message.contains("Die")) {
					die = true;
					bot.getTaskManager().stopAll();
					bot.setActivity(null);
				} else if(message.contains("Say ")) {
					connectionHandler.sendPacket(new Packet3Chat(message.substring(message.indexOf("Say ") + "Say ".length())));
				} else if(message.contains("Leave")) {
					connectionHandler.sendPacket(new Packet255KickDisconnect("Quit"));
				} else if(message.contains("Tool")) {
					MainPlayerEntity player = bot.getPlayer();
					if(player == null)
						return;
					PlayerInventory inventory = player.getInventory();
					inventory.setCurrentHeldSlot(Integer.parseInt(message.substring(message.indexOf("Tool ") + "Tool ".length()).split(" ")[0]));
				} else if(message.contains("DropId ")) {
					MainPlayerEntity player = bot.getPlayer();
					PlayerInventory inventory = player.getInventory();
					String substring = message.substring(message.indexOf("DropId ") + "DropId ".length()).split(" ")[0];
					int id = Integer.parseInt(substring);
					for(int slot = 0; slot < 40; slot++) {
						ItemStack item = inventory.getItemAt(slot);
						if(item != null && item.getId() == id) {
							inventory.selectItemAt(slot, true);
							inventory.dropSelectedItem();
						}
					}
					inventory.close();
				} else if(message.contains("Drop")) {
					MainPlayerEntity player = bot.getPlayer();
					PlayerInventory inventory = player.getInventory();
					if(message.contains("Drop ")) {
						String substring = message.substring(message.indexOf("Drop ") + "Drop ".length()).split(" ")[0];
						try {
							int slot = Integer.parseInt(substring);
							if(slot < 0 || slot >= 40)
								return;
							if(inventory.getItemAt(slot) != null) {
								inventory.selectItemAt(slot, true);
								inventory.dropSelectedItem();
							}
							return;
						} catch(NumberFormatException e) {}
					}
					for(int slot = 0; slot < 40; slot++) {
						if(inventory.getItemAt(slot) != null) {
							inventory.selectItemAt(slot, true);
							inventory.dropSelectedItem();
						}
					}
					inventory.close();
				} else if(message.contains("Switch ")) {
					MainPlayerEntity player = bot.getPlayer();
					PlayerInventory inventory = player.getInventory();
					String substring = message.substring(message.indexOf("Switch ") + "Switch ".length());
					try {
						int slot1 = Integer.parseInt(substring.split(" ")[0]);
						int slot2 = Integer.parseInt(substring.split(" ")[1]);
						if(slot1 < 0 || slot1 >= 45 || slot2 < 0 || slot2 >= 45)
							return;
						inventory.selectItemAt(slot1);
						inventory.selectItemAt(slot2);
						inventory.selectItemAt(slot1);
					} catch(NumberFormatException e) {}
					// inventory.close();
				} else if(message.contains("Equip")) {
					MainPlayerEntity player = bot.getPlayer();
					PlayerInventory inventory = player.getInventory();
					boolean helmet = inventory.getArmorAt(0) != null;
					boolean chestplate = inventory.getArmorAt(1) != null;
					boolean leggings = inventory.getArmorAt(2) != null;
					boolean boots = inventory.getArmorAt(3) != null;
					boolean changed = false;
					for(int i = 0; i < 36; i++) {
						ItemStack item = inventory.getItemAt(i);
						if(item == null)
							continue;
						int armorSlot;
						int id = item.getId();
						if(!helmet && (id == 86 || id == 298 || id == 302 || id == 306 || id == 310 || id == 314)) {
							armorSlot = 0;
							helmet = true;
						} else if(!chestplate && (id == 299 || id == 303 || id == 307 || id == 311 || id == 315)) {
							armorSlot = 1;
							chestplate = true;
						} else if(!leggings && (id == 300 || id == 304 || id == 308 || id == 312 || id == 316)) {
							armorSlot = 2;
							leggings = true;
						} else if(!boots && (id == 301 || id == 305 || id == 309 || id == 313 || id == 317)) {
							armorSlot = 3;
							boots = true;
						} else if(helmet && chestplate && leggings && boots)
							break;
						else
							continue;
						inventory.selectItemAt(i);
						inventory.selectArmorAt(armorSlot);
						changed = true;
					}
					if(!changed) {
						for(int i = 0; i < 36; i++) {
							ItemStack item = inventory.getItemAt(i);
							if(item != null)
								continue;
							int armorSlot;
							if(helmet) {
								armorSlot = 0;
								helmet = false;
							} else if(chestplate) {
								armorSlot = 1;
								chestplate = false;
							} else if(leggings) {
								armorSlot = 2;
								leggings = false;
							} else if(boots) {
								armorSlot = 3;
								boots = false;
							} else if(!helmet && !chestplate && !leggings && !boots)
								break;
							else
								continue;
							inventory.selectArmorAt(armorSlot);
							inventory.selectItemAt(i);
						}
					}
					inventory.close();
					bot.say("/msg " + owner + " Equipped armor.");
				} else if(message.contains("Owner ")) {
					String name = message.substring(message.indexOf("Owner ") + "Owner ".length()).split(" ")[0];
					owner = name;
					bot.say("/msg " + owner + " Set owner to " + name);
				} else if(message.contains("Follow")) {
					String[] args = new String[0];
					if(message.trim().contains("Follow "))
						args = message.substring(message.indexOf("Follow ") + "Follow ".length()).split(" ");
					String name = owner;
					if(args.length > 0) {
						name = args[0];
						if(name.equalsIgnoreCase(owner)) {
							name = owner;
							args = new String[0];
						}
					}
					FollowTask followTask = bot.getTaskManager().getTaskFor(FollowTask.class);
					if(followTask.isActive())
						followTask.stop();
					for(Entity entity : bot.getWorld().getEntities()) {
						if(entity instanceof PlayerEntity && Util.stripColors(((PlayerEntity) entity).getName()).equalsIgnoreCase(name)) {
							followTask.follow(entity);
							bot.say("/msg " + owner + " Following " + (args.length > 0 ? Util.stripColors(((PlayerEntity) entity).getName()) : "you") + ".");
							return;
						}
					}
					bot.say("/msg " + owner + " Player " + name + " not found.");
				} else if(message.contains("Attack ")) {
					String[] args = message.substring(message.indexOf("Attack ") + "Attack ".length()).split(" ");
					String name = args[0];
					AttackTask attackTask = bot.getTaskManager().getTaskFor(AttackTask.class);
					for(Entity entity : bot.getWorld().getEntities()) {
						if(entity instanceof PlayerEntity && Util.stripColors(((PlayerEntity) entity).getName()).equalsIgnoreCase(name)) {
							attackTask.setAttackEntity(entity);
							bot.say("/msg " + owner + " Attacking " + Util.stripColors(((PlayerEntity) entity).getName()) + "!");
							return;
						}
					}
					bot.say("/msg " + owner + " Player " + name + " not found.");
				} else if(message.contains("Walk ")) {
					String[] args = message.substring(message.indexOf("Walk ") + "Walk ".length()).split(" ");
					MainPlayerEntity player = bot.getPlayer();
					BlockLocation location = new BlockLocation(player.getLocation());
					boolean relativeX = args[0].charAt(0) == '+', relativeZ = args[args.length - 1].charAt(0) == '+';
					int x, y, z;

					if(relativeX)
						x = location.getX() + Integer.parseInt(args[0].substring(1));
					else
						x = Integer.parseInt(args[0]);

					if(relativeZ)
						z = location.getZ() + Integer.parseInt(args[args.length - 1].substring(1));
					else
						z = Integer.parseInt(args[args.length - 1]);

					if(args.length < 3) {
						World world = bot.getWorld();
						for(y = 256; y > 0; y--) {
							int id = world.getBlockIdAt(x, y - 1, z);
							if(BlockType.getById(id).isSolid())
								break;
						}
						if(y <= 0) {
							bot.say("/msg " + owner + " No appropriate walkable y value!");
							return;
						}
					} else
						y = Integer.parseInt(args[1]);

					BlockLocation target = new BlockLocation(x, y, z);
					player.getLocation().getDistanceTo(new WorldLocation(target));
					bot.setActivity(new WalkActivity(bot, target));
					bot.say("/msg " + owner + " Walking to (" + x + ", " + y + ", " + z + ").");
				} else if(message.contains("AttackAll")) {
					HostileTask task = bot.getTaskManager().getTaskFor(HostileTask.class);
					task.start();
					bot.say("/msg " + owner + " Now in hostile mode!");
				}
			} else if(message.contains("You are not member of any faction.") && spamMessage != null && createFaction) {
				String msg = "/f create " + Util.generateRandomString(7 + random.nextInt(4));
				bot.say(msg);
			}
			break;
		case 8:
			Packet8UpdateHealth updateHealth = (Packet8UpdateHealth) packet;
			if(updateHealth.healthMP <= 0)
				connectionHandler.sendPacket(new Packet205ClientCommand(1));
			break;
		case 9:
			TaskManager taskManager = bot.getTaskManager();
			taskManager.stopAll();
			bot.setActivity(null);
			break;
		}
	}

	public MinecraftBot getBot() {
		return bot;
	}

	@EventHandler
	public void onSpawn(SpawnEvent event) {
		bot.getPlayer().getInventory().setDelay(500);
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		System.out.println("[" + bot.getSession().getUsername() + "] Disconnected: " + event.getReason());
		bot.getService().shutdownNow();
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if(!bot.hasSpawned() || !bot.isConnected())
			return;
		if(ticksToGo > 0) {
			ticksToGo--;
			return;
		}

		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		if(firstStart) {
			bot.say("/pay DarkStorm_ 1000");
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			/*try {
				PlayerInventory inventory = player.getInventory();
				for(int i = 0; i < 44; i++) {
					ItemStack item = inventory.getItemAt(i);
					if(item != null) {
						inventory.selectItemAt(i);
						inventory.dropSelectedItem();
					}
				}
			} catch(Exception exception) {}*/
			connectionHandler.sendPacket(new Packet255KickDisconnect("Quit"));
			ticksToGo = 15;
			firstStart = false;
			return;
		} else
			canSpam = true;
		if(canSpam) {
			if(die) {
				connectionHandler.sendPacket(new Packet255KickDisconnect("Quit"));
				ticksToGo = 15;
				return;
			}
			if(!bot.hasSpawned())
				return;
			if(spamMessage == null)
				return;
			if(nextMessage > 0) {
				nextMessage--;
				return;
			}
			String message = spamMessage;
			MessageFormatter formatter = new MessageFormatter();
			String botName;
			synchronized(bots) {
				botName = bots.get(nextBot++).bot.getSession().getUsername();
				if(nextBot >= bots.size())
					nextBot = 0;
			}
			formatter.setVariable("bot", botName);
			if(spamList.length > 0) {
				formatter.setVariable("spamlist", spamList[nextSpamList++]);
				if(nextSpamList >= spamList.length)
					nextSpamList = 0;
			}
			formatter.setVariable("rnd", Util.generateRandomString(15 + random.nextInt(6)));
			formatter.setVariable("msg", Character.toString(msgChars[nextMsgChar++]));
			if(nextMsgChar >= msgChars.length)
				nextMsgChar = 0;
			message = formatter.format(message);
			connectionHandler.sendPacket(new Packet3Chat(message));
		}
	}

	public static void main(String[] args) {
		// TODO main
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("h", "help"), "Show this help dialog.");
		OptionSpec<String> serverOption = parser.acceptsAll(Arrays.asList("s", "server"), "Server to join.").withRequiredArg().describedAs("server-address[:port]");
		OptionSpec<String> proxyOption = parser.acceptsAll(Arrays.asList("P", "proxy"), "SOCKS proxy to use. Ignored in presence of 'socks-proxy-list'.").withRequiredArg().describedAs("proxy-address");
		OptionSpec<String> ownerOption = parser.acceptsAll(Arrays.asList("o", "owner"), "Owner of the bot (username of in-game control).").withRequiredArg().describedAs("username");
		OptionSpec<?> offlineOption = parser.acceptsAll(Arrays.asList("O", "offline"), "Offline-mode. Ignores 'password' and 'account-list' (will " + "generate random usernames if 'username' is not supplied).");
		OptionSpec<?> autoRejoinOption = parser.acceptsAll(Arrays.asList("a", "auto-rejoin"), "Auto-rejoin a server on disconnect.");
		OptionSpec<Integer> loginDelayOption = parser.acceptsAll(Arrays.asList("d", "login-delay"), "Delay between bot joins, in milliseconds. 5000 is " + "recommended if not using socks proxies.").withRequiredArg().describedAs("delay").ofType(Integer.class);
		OptionSpec<Integer> botAmountOption = parser.acceptsAll(Arrays.asList("b", "bot-amount"), "Amount of bots to join. Must be <= amount of accounts.").withRequiredArg().describedAs("amount").ofType(Integer.class);

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

		final List<String> socksProxies;
		if(options.has(socksProxyListOption))
			socksProxies = loadProxies(options.valueOf(socksProxyListOption));
		else
			socksProxies = null;
		final boolean useProxy = socksProxies != null;

		final List<String> httpProxies;
		if(options.has(httpProxyListOption))
			httpProxies = loadLoginProxies(options.valueOf(httpProxyListOption));
		else if(!offline && accounts != null) {
			System.out.println("Option 'http-proxy-list' required if " + "option 'account-list' is supplied.");
			printHelp(parser);
			return;
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

		final Queue<Runnable> lockQueue = new ArrayDeque<Runnable>();

		ExecutorService service = Executors.newFixedThreadPool(botAmount + (loginDelay > 0 ? 1 : 0));
		final Object firstWait = new Object();
		if(loginDelay > 0) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					synchronized(firstWait) {
						try {
							firstWait.wait();
						} catch(InterruptedException exception) {}
					}
					while(true) {
						if(die)
							return;
						while(slotsTaken.get() >= 2) {
							synchronized(slotsTaken) {
								try {
									slotsTaken.wait(500);
								} catch(InterruptedException exception) {}
							}
						}
						synchronized(lockQueue) {
							if(lockQueue.size() > 0) {
								Runnable thread = lockQueue.poll();
								synchronized(thread) {
									thread.notifyAll();
								}
								lockQueue.offer(thread);
							} else
								continue;
						}
						try {
							Thread.sleep(loginDelay);
						} catch(InterruptedException exception) {}
						while(!sessions.get()) {
							synchronized(sessions) {
								try {
									sessions.wait(5000);
								} catch(InterruptedException exception) {}
							}
						}
					}
				}
			});
		}
		final List<String> accountsInUse = new ArrayList<String>();
		for(int i = 0; i < botAmount; i++) {
			final int botNumber = i;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if(loginDelay > 0)
						synchronized(lockQueue) {
							lockQueue.add(this);
						}
					Random random = new Random();

					if(!offline) {
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
								loginProxy = httpProxies.get(random.nextInt(httpProxies.size()));
								try {
									session = Util.retrieveSession(accountParts[0], accountParts[1], loginProxy);
									// addAccount(session);
									sessionCount.incrementAndGet();
									authenticated = true;
									break;
								} catch(AuthenticationException exception) {
									System.err.println("[Bot" + botNumber + "] " + exception);
									if(!exception.getMessage().startsWith("Exception"))
										// && !exception.getMessage().equals(
										// "Too many failed logins"))
										continue user;
								}
							}
							System.out.println("[" + session.getUsername() + "] Password: " + session.getPassword() + ", Session ID: " + session.getSessionId());
							while(!joins.get()) {
								synchronized(joins) {
									try {
										joins.wait(5000);
									} catch(InterruptedException exception) {}
								}
							}
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

							while(true) {
								String proxy = useProxy ? socksProxies.get(random.nextInt(socksProxies.size())) : null;
								try {
									new DarkBotMCSpambot(DARK_BOT, server, session.getUsername(), session.getPassword(), session.getSessionId(), null, proxy, owner);
									if(die)
										break user;
									else if(!autoRejoin)
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
							String proxy = useProxy ? socksProxies.get(random.nextInt(socksProxies.size())) : null;
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
								new DarkBotMCSpambot(DARK_BOT, server, username, "", "", null, proxy, owner);
								if(die || !autoRejoin)
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
				joinsButton.setText(joinsButton.getText().split(" ")[0] + " (" + Integer.toString(amountJoined.get()) + ")");
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

	private static final class MessageFormatter {
		private static final Pattern variablePattern = Pattern.compile("(?i)\\$(\\{[a-z0-9_]{1,}\\}|[a-z0-9_]{1,})");
		private static final Pattern colorPattern = Pattern.compile("(?i)&[0-9A-FK-OR]");

		private final Map<String, String> variables;

		public MessageFormatter() {
			variables = new HashMap<String, String>();
		}

		public synchronized void setVariable(String variable, String value) {
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