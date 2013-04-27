package org.darkstorm.darkbot.mcspambot;

import java.util.*;
import java.util.regex.*;

import java.io.*;

import javax.naming.AuthenticationException;

import joptsimple.*;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.events.world.SpawnEvent;
import org.darkstorm.darkbot.minecraftbot.handlers.*;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.*;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.util.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData.ProxyType;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class DarkBotMC implements EventListener, GameListener {
	private static final char[] alphas = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
	public static final DarkBot DARK_BOT = new DarkBot();
	private final List<String> users = new ArrayList<String>();

	private MinecraftBot bot;
	private ConnectionHandler connectionHandler;

	private DarkBotMC(DarkBot darkBot, String server, String username,
			String password, String sessionId, String loginProxy, String proxy,
			String owner) {
		MinecraftBotData botData = new MinecraftBotData();
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
			botData.proxy = new ProxyData(proxy, port, type);
			// this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(
			// proxy, port));
		}
		if(loginProxy != null && !loginProxy.isEmpty()) {
			int port = 80;
			if(loginProxy.contains(":")) {
				String[] parts = loginProxy.split(":");
				loginProxy = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			botData.loginProxy = loginProxy;
			botData.loginProxyPort = port;
			// this.loginProxy = new Proxy(Proxy.Type.HTTP, new
			// InetSocketAddress(
			// loginProxy, port));
		}
		botData.nickname = username;
		botData.password = password;
		if(sessionId != null) {
			botData.sessionId = sessionId;
			botData.authenticate = false;
		} else
			botData.authenticate = true;
		if(server != null && !server.isEmpty()) {
			int port = 25565;
			if(server.contains(":")) {
				String[] parts = server.split(":");
				server = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			botData.server = server;
			botData.port = port;
		} else
			throw new IllegalArgumentException("Unknown server!");

		botData.owner = owner;
		System.out.println("[" + username + "] Connecting...");
		bot = new MinecraftBot(darkBot, botData);
		connectionHandler = bot.getConnectionHandler();
		System.gc();
		System.out.println("[" + username + "] Joined!");
		TaskManager taskManager = bot.getTaskManager();
		taskManager.registerTask(new WalkTask(bot));
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
		bot.getEventManager().registerListener(this);
		bot.getGameHandler().registerListener(this);
		// new MCToIRCController(this);
		while(bot.isConnected()) {
			try {
				Thread.sleep(1500);
			} catch(InterruptedException exception) {
				exception.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		switch(packet.getId()) {
		case 0:
			connectionHandler.sendPacket(new Packet0KeepAlive(new Random()
					.nextInt()));
			break;
		case 3:
			String message = ((Packet3Chat) packet).message;
			message = removeColors(message);
			System.out.println("[" + bot.getSession().getUsername() + "] "
					+ message);
			String nocheat = "Please type '([^']*)' to continue sending messages/commands\\.";
			Matcher nocheatMatcher = Pattern.compile(nocheat).matcher(message);
			if(nocheatMatcher.matches()) {
				try {
					String captcha = nocheatMatcher.group(1);
					connectionHandler.sendPacket(new Packet3Chat(captcha));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} else if(message.startsWith("/uc ")) {
				connectionHandler.sendPacket(new Packet3Chat(message));
			} else if(message.contains(bot.getOwner())) {
				if(message.contains("Stop")) {
					bot.getTaskManager().stopAll();
					bot.say("Stopped all tasks.");
				} else if(message.contains("Die")) {
					bot.say("Leaving!");
					connectionHandler.sendPacket(new Packet255KickDisconnect(
							"Quit"));
				} else if(message.contains("Say ")) {
					bot.say(message.substring(message.indexOf("Say ")
							+ "Say ".length()));
				} else if(message.contains("Leave")) {
					bot.say("Leaving!");
					connectionHandler.sendPacket(new Packet255KickDisconnect(
							"Quit"));
				} else if(message.contains("Goto ")) {
					System.out.println(message);
					String[] parts = message.substring(
							message.indexOf("Goto ") + "Goto ".length()).split(
							" ");
					WalkTask task = bot.getTaskManager().getTaskFor(
							WalkTask.class);
					BlockLocation target = new BlockLocation(
							Integer.parseInt(parts[0]),
							Integer.parseInt(parts[1]),
							Integer.parseInt(parts[2]));
					if(task.isActive())
						task.stop();
					task.setTarget(target);
				} else if(message.contains("Chop")) {
					bot.getTaskManager().getTaskFor(ChopTreesTask.class)
							.start();
				} else if(message.contains("Tool ")) {
					MainPlayerEntity player = bot.getPlayer();
					if(player == null)
						return;
					PlayerInventory inventory = player.getInventory();
					inventory
							.setCurrentHeldSlot(Integer.parseInt(message
									.substring(
											message.indexOf("Tool ")
													+ "Tool ".length()).split(
											" ")[0]));
				} else if(message.contains("DropId ")) {
					MainPlayerEntity player = bot.getPlayer();
					PlayerInventory inventory = player.getInventory();
					String substring = message.substring(
							message.indexOf("DropId ") + "DropId ".length())
							.split(" ")[0];
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
						String substring = message.substring(
								message.indexOf("Drop ") + "Drop ".length())
								.split(" ")[0];
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
					String substring = message.substring(message
							.indexOf("Switch ") + "Switch ".length());
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
				} else if(message.contains("Follow")) {
					String substring = message.substring(message
							.indexOf("Follow") + "Follow".length());
					String name = bot.getOwner();
					if(substring.contains(" ")) {
						name = substring.split(" ")[1];
					}
					FollowTask followTask = bot.getTaskManager().getTaskFor(
							FollowTask.class);
					if(followTask.isActive())
						followTask.stop();
					for(Entity entity : bot.getWorld().getEntities()) {
						if(entity instanceof PlayerEntity
								&& removeColors(
										((PlayerEntity) entity).getName())
										.equalsIgnoreCase(name)) {
							followTask.follow(entity);
							bot.say("Following "
									+ (substring.contains(" ") ? removeColors(((PlayerEntity) entity)
											.getName()) : "you") + ".");
							return;
						}
					}
					bot.say("Player " + name + " not found.");
				} else if(message.contains("Attack ")) {
					String name = message.substring(
							message.indexOf("Attack ") + "Attack ".length())
							.split(" ")[0];
					AttackTask attackTask = bot.getTaskManager().getTaskFor(
							AttackTask.class);
					for(Entity entity : bot.getWorld().getEntities()) {
						if(entity instanceof PlayerEntity
								&& removeColors(
										((PlayerEntity) entity).getName())
										.equalsIgnoreCase(name)) {
							attackTask.setAttackEntity(entity);
							bot.say("Attacking "
									+ removeColors(((PlayerEntity) entity)
											.getName()) + "!");
							return;
						}
					}
					bot.say("Player " + name + " not found.");
				} else if(message.contains("AttackAll")) {
					HostileTask task = bot.getTaskManager().getTaskFor(
							HostileTask.class);
					if(task.isActive()) {
						task.stop();
						bot.say("No longer in hostile mode.");
					} else {
						task.start();
						bot.say("Now in hostile mode!");
					}
				} else if(message.contains("Mine")) {
					MiningTask task = bot.getTaskManager().getTaskFor(
							MiningTask.class);
					if(task.isActive()) {
						task.stop();
						bot.say("No longer mining.");
					} else {
						task.start();
						bot.say("Now mining!");
					}
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
						if(!helmet
								&& (id == 86 || id == 298 || id == 302
										|| id == 306 || id == 310 || id == 314)) {
							armorSlot = 0;
							helmet = true;
						} else if(!chestplate
								&& (id == 299 || id == 303 || id == 307
										|| id == 311 || id == 315)) {
							armorSlot = 1;
							chestplate = true;
						} else if(!leggings
								&& (id == 300 || id == 304 || id == 308
										|| id == 312 || id == 316)) {
							armorSlot = 2;
							leggings = true;
						} else if(!boots
								&& (id == 301 || id == 305 || id == 309
										|| id == 313 || id == 317)) {
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
							} else if(!helmet && !chestplate && !leggings
									&& !boots)
								break;
							else
								continue;
							inventory.selectArmorAt(armorSlot);
							inventory.selectItemAt(i);
						}
					}
					inventory.close();
					bot.say("Equipped armor.");
				} else if(message.contains("Fish")) {
					FishingTask task = bot.getTaskManager().getTaskFor(
							FishingTask.class);
					if(task.isActive()) {
						task.stop();
						bot.say("No longer fishing.");
					} else {
						task.start();
						bot.say("Now fishing!");
					}
				} else if(message.contains("Farm")) {
					FarmingTask task = bot.getTaskManager().getTaskFor(
							FarmingTask.class);
					if(task.isActive()) {
						task.stop();
						bot.say("No longer farming.");
					} else {
						task.start();
						bot.say("Now farming!");
					}
				} else if(message.contains("Levels")) {
					MainPlayerEntity player = bot.getPlayer();
					if(player == null)
						return;
					bot.say("Level " + player.getExperienceLevel() + " ("
							+ player.getExperienceTotal() + " total exp.)");
				} else if(message.contains("Health")) {
					MainPlayerEntity player = bot.getPlayer();
					if(player == null)
						return;

					bot.say("Health: [" + player.getHealth() + "/20] Hunger: ["
							+ player.getHunger() + "/20]");
				} else if(message.contains("Tasks")) {
					try {
						String tasks = "";
						String activeTasks = "";
						TaskManager manager = bot.getTaskManager();
						for(Task task : manager.getRegisteredTasks()) {
							tasks += task.getClass().getSimpleName() + ", ";
							if(task.isActive())
								activeTasks += task.getClass().getSimpleName()
										+ ", ";
						}
						if(!tasks.isEmpty())
							tasks = tasks.substring(0, tasks.length() - 2);
						if(!activeTasks.isEmpty())
							activeTasks = activeTasks.substring(0,
									activeTasks.length() - 2);
						System.out.println("Tasks: [" + tasks + "] Active: ["
								+ activeTasks + "]");
						bot.say("Tasks: [" + tasks + "] Active: ["
								+ activeTasks + "]");
					} catch(Exception exception) {
						exception.printStackTrace();
					}
				} else if(message.contains("SetOwner ")) {
					String name = message
							.substring(
									message.indexOf("SetOwner ")
											+ "SetOwner ".length()).split(" ")[0];
					bot.setOwner(name);
					bot.say("Set owner to " + name);
				} else if(message.contains("Hit ")) {
					MainPlayerEntity player = bot.getPlayer();
					String substring = message.substring(message
							.indexOf("Hit ") + "Hit ".length());
					try {
						int x = Integer.parseInt(substring.split(" ")[0]);
						int y = Integer.parseInt(substring.split(" ")[1]);
						int z = Integer.parseInt(substring.split(" ")[1]);

						player.face(x, y, z);
						ConnectionHandler connectionHandler = bot
								.getConnectionHandler();
						connectionHandler.sendPacket(new Packet12PlayerLook(
								(float) player.getYaw(), (float) player
										.getPitch(), true));
						connectionHandler.sendPacket(new Packet18Animation(
								player.getId(), Animation.SWING_ARM));
						connectionHandler.sendPacket(new Packet14BlockDig(0, x,
								y, z, 0));
					} catch(NumberFormatException e) {}
				} else if(message.contains("Break ")) {
					MainPlayerEntity player = bot.getPlayer();
					String substring = message.substring(message
							.indexOf("Break ") + "Break ".length());
					try {
						int x = Integer.parseInt(substring.split(" ")[0]);
						int y = Integer.parseInt(substring.split(" ")[1]);
						int z = Integer.parseInt(substring.split(" ")[1]);

						player.face(x, y, z);
						ConnectionHandler connectionHandler = bot
								.getConnectionHandler();
						connectionHandler.sendPacket(new Packet12PlayerLook(
								(float) player.getYaw(), (float) player
										.getPitch(), true));
						connectionHandler.sendPacket(new Packet18Animation(
								player.getId(), Animation.SWING_ARM));
						connectionHandler.sendPacket(new Packet14BlockDig(0, x,
								y, z, 0));
						connectionHandler.sendPacket(new Packet14BlockDig(2, x,
								y, z, 0));
					} catch(NumberFormatException e) {}
				} else if(message.contains("Use ")) {
					MainPlayerEntity player = bot.getPlayer();
					PlayerInventory inventory = player.getInventory();
					String substring = message.substring(message
							.indexOf("Use ") + "Use ".length());
					try {
						int x = Integer.parseInt(substring.split(" ")[0]);
						int y = Integer.parseInt(substring.split(" ")[1]);
						int z = Integer.parseInt(substring.split(" ")[1]);

						player.face(x, y, z);
						ConnectionHandler connectionHandler = bot
								.getConnectionHandler();
						connectionHandler.sendPacket(new Packet12PlayerLook(
								(float) player.getYaw(), (float) player
										.getPitch(), true));
						connectionHandler.sendPacket(new Packet18Animation(
								player.getId(), Animation.SWING_ARM));

						Packet15Place placePacket = new Packet15Place();
						placePacket.xPosition = x;
						placePacket.yPosition = y + 1;
						placePacket.zPosition = z;
						placePacket.direction = 0;
						placePacket.itemStack = inventory.getCurrentHeldItem();
						connectionHandler.sendPacket(placePacket);
					} catch(NumberFormatException e) {}
				} else if(message.contains("Players"))
					requestPlayers();
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
			break;
		case 201:
			Packet201PlayerInfo infoPacket = (Packet201PlayerInfo) packet;
			if(infoPacket.isConnected && !users.contains(infoPacket.playerName)) {
				users.add(infoPacket.playerName);
				if(infoPacket.ping == 1000) {
					if(infoPacket.playerName.equalsIgnoreCase(bot.getSession()
							.getUsername()))
						return;
				}
			} else if(!infoPacket.isConnected
					&& users.contains(infoPacket.playerName))
				users.remove(infoPacket.playerName);
			break;
		}
	}

	private String removeColors(final String input) {
		if(input != null)
			return input.replaceAll("(?i)§[0-9a-fk-or]", "");
		return null;
	}

	public MinecraftBot getBot() {
		return bot;
	}

	private void requestPlayers() {
		String players = users.toString();
		players = players.substring(1, players.length() - 1);
		List<String> lines = new ArrayList<String>();
		String[] parts = players.split(", ");
		String current = "";
		for(int i = 0; i < parts.length; i++) {
			if(current.length() + parts[i].length() + 2 >= 100) {
				lines.add(current);
				current = parts[i] + ", ";
			} else
				current += parts[i] + ", ";
		}
		if(!current.isEmpty()) {
			current = current.substring(0, current.length() - 2);
			lines.add(current);
		}

		bot.say("Players:");
		for(String line : lines)
			bot.say(line);
		System.out.println(">>>Players: " + players.replace(",", ""));
	}

	@EventHandler
	public void onSpawn(SpawnEvent event) {
		MainPlayerEntity player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		inventory.setDelay(150);
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		System.out.println("[" + bot.getSession().getUsername()
				+ "] Disconnected: " + event.getReason());
		bot.getService().shutdownNow();
	}

	double distanceToNearestLog = Double.MAX_VALUE;

	boolean firstStart = true;
	int ticksToGo = 20;
	boolean canSpam = false;

	double lastYaw, lastPitch;
	int ticksLeftTillMove = 100;

	BlockLocation spawnLocation = new BlockLocation(231, 71, -39);
	BlockLocation targetLocation = new BlockLocation(230, 72, -30);

	public void onTick() {
		if(!bot.hasSpawned() || !bot.isConnected())
			return;
		if(ticksToGo > 0) {
			ticksToGo--;
			return;
		}

		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		if("1".equals("")) {
			WalkTask task = bot.getTaskManager().getTaskFor(WalkTask.class);
			if(player.getDistanceTo(spawnLocation) < 5) {
				if(!task.isMoving())
					task.setTarget(targetLocation);
				canSpam = false;
			} else
				canSpam = true;
		} else if("1".equals("")) {
			// Drop all and disconnect
			canSpam = true;
			PlayerInventory inventory = player.getInventory();
			for(int i = 0; i < inventory.getSize(); i++) {
				if(inventory.getItemAt(i) != null) {
					inventory.selectItemAt(i);
					inventory.dropSelectedItem();
				}
			}
			connectionHandler.sendPacket(new Packet3Chat("\247disconnect"));
		} else
			canSpam = true;
	}

	public static void main(String[] args) {
		// TODO main
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("h", "help"), "Show this help dialog.");
		OptionSpec<String> serverOption = parser
				.acceptsAll(Arrays.asList("s", "server"), "Server to join.")
				.withRequiredArg().describedAs("server-address[:port]");
		OptionSpec<String> proxyOption = parser
				.acceptsAll(Arrays.asList("P", "proxy"),
						"SOCKS proxy to use. Ignored in presence of 'socks-proxy-list'.")
				.withRequiredArg().describedAs("proxy-address");
		OptionSpec<String> ownerOption = parser
				.acceptsAll(Arrays.asList("o", "owner"),
						"Owner of the bot (username of in-game control).")
				.withRequiredArg().describedAs("username");
		OptionSpec<String> usernameOption = parser
				.acceptsAll(Arrays.asList("u", "username"),
						"Bot username. Ignored in presence of 'account-list'.")
				.withRequiredArg().describedAs("username/email");
		OptionSpec<String> passwordOption = parser
				.acceptsAll(
						Arrays.asList("p", "password"),
						"Bot password. Ignored in presence of 'offline' or "
								+ "'account-list', or if 'username' is not supplied.")
				.withRequiredArg().describedAs("password");
		OptionSpec<?> offlineOption = parser
				.acceptsAll(
						Arrays.asList("O", "offline"),
						"Offline-mode. Ignores 'password' and 'account-list' (will "
								+ "generate random usernames if 'username' is not supplied).");
		OptionSpec<?> autoRejoinOption = parser.acceptsAll(
				Arrays.asList("a", "auto-rejoin"),
				"Auto-rejoin a server on disconnect.");

		OptionSpec<String> accountListOption = parser
				.accepts("account-list",
						"File containing a list of accounts, in username/email:password format.")
				.withRequiredArg().describedAs("file");
		OptionSpec<String> socksProxyListOption = parser
				.accepts("socks-proxy-list",
						"File containing a list of SOCKS proxies, in address:port format.")
				.withRequiredArg().describedAs("file");
		OptionSpec<String> httpProxyListOption = parser
				.accepts("http-proxy-list",
						"File containing a list of HTTP proxies, in address:port format.")
				.withRequiredArg().describedAs("file");

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
					System.out.println("Option 'password' or option "
							+ "'offline' required.");
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
			System.out.println("Option 'http-proxy-list' required in presence "
					+ "of option 'account-list'.");
			printHelp(parser);
			return;
		} else
			httpProxies = null;

		final List<String> accountsInUse = new ArrayList<String>();
		Random random = new Random();

		if(!offline) {
			user: do {
				Session session = null;
				String loginProxy;
				String account;
				if(username == null) {
					account = accounts.get(random.nextInt(accounts.size()));
					synchronized(accountsInUse) {
						while(accountsInUse.contains(account))
							account = accounts.get(random.nextInt(accounts
									.size()));
						accountsInUse.add(account);
					}
				} else
					account = username + ":" + password;
				String[] accountParts = account.split(":");
				while(true) {
					loginProxy = username != null ? null : httpProxies
							.get(random.nextInt(httpProxies.size()));
					try {
						session = Util.retrieveSession(accountParts[0],
								accountParts[1], loginProxy);
						break;
					} catch(AuthenticationException exception) {
						System.err.println("[Bot] " + exception);
						if(username != null)
							break user;
						if(!exception.getMessage().startsWith("Exception"))
							// && !exception.getMessage().equals(
							// "Too many failed logins"))
							continue user;
					}
				}
				System.out.println("[" + session.getUsername() + "] Password: "
						+ session.getPassword() + ", Session ID: "
						+ session.getSessionId());

				while(true) {
					String proxy = useProxy ? defaultProxy != null ? defaultProxy
							: socksProxies.get(random.nextInt(socksProxies
									.size()))
							: null;
					try {
						new DarkBotMC(DARK_BOT, server, session.getUsername(),
								session.getPassword(), session.getSessionId(),
								null, proxy, owner);
						if(!autoRejoin)
							break;
					} catch(Exception exception) {
						exception.printStackTrace();
						System.out.println("[" + session.getUsername()
								+ "] Error connecting: "
								+ exception.getCause().toString());
					}
				}
				System.out.println("[" + session.getUsername()
						+ "] Account failed");
			} while(username == null);
		} else {
			while(true) {
				String proxy = useProxy ? socksProxies.get(random
						.nextInt(socksProxies.size())) : null;
				try {
					String name = "";
					if(username == null) {
						for(int i = 0; i < 10 + random.nextInt(6); i++)
							name += alphas[random.nextInt(alphas.length)];
					} else
						name = username;
					new DarkBotMC(DARK_BOT, server, name, "", "", null, proxy,
							owner);
					if(!autoRejoin)
						break;
					else
						continue;
				} catch(Exception exception) {
					System.out.println("[Bot] Error connecting: "
							+ exception.toString());
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
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					fileName)));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty())
					continue;
				String[] parts = line.split(" ")[0].trim().split(":");
				proxies.add(parts[0].trim() + ":"
						+ Integer.parseInt(parts[1].trim()));
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
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					fileName)));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty())
					continue;
				String[] parts = line.split(" ")[0].trim().split(":");
				proxies.add(parts[0].trim() + ":"
						+ Integer.parseInt(parts[1].trim()));
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
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					fileName)));
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
}