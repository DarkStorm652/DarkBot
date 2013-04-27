package org.darkstorm.darkbot.mcspambot;

import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.naming.AuthenticationException;
import javax.script.*;
import javax.swing.*;
import javax.swing.Timer;

import joptsimple.*;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.ai.TaskManager;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.events.world.SpawnEvent;
import org.darkstorm.darkbot.minecraftbot.handlers.*;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.Packet8UpdateHealth;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet205ClientCommand;
import org.darkstorm.darkbot.minecraftbot.util.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData.ProxyType;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

@SuppressWarnings("unused")
public class DarkBotMCSpambot implements EventListener, GameListener {
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
	private static final char[] msgChars = { 'a', 'e', 'i', 'o', 'u' };
	private static final AtomicInteger amountJoined = new AtomicInteger();
	private static final String[] skills = new String[] { "", "mining",
			"woodcutting", "herbalism", "unarmed", "archery", "swords",
			"repair", "excavation", "fishing", "abilities", "parties",
			"leveling", "axes", "taming", "repair" };
	private static final boolean createFaction = true;
	public static final DarkBot DARK_BOT = new DarkBot();
	private static final List<DarkBotMCSpambot> bots = new ArrayList<DarkBotMCSpambot>();

	private static final String[] spamList;

	private static AtomicInteger slotsTaken = new AtomicInteger();

	static {
		List<String> spamlist = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					"spamlist.txt")));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty())
					continue;
				spamlist.add(line);
			}
			reader.close();
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
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

	private DarkBotMCSpambot(DarkBot darkBot, String server, String username,
			String password, String sessionId, String loginProxy, String proxy,
			String owner) {
		synchronized(bots) {
			bots.add(this);
			// slotsTaken.incrementAndGet();
			synchronized(slotsTaken) {
				slotsTaken.notifyAll();
			}
		}
		MinecraftBotData botData = new MinecraftBotData();
		// botData.nickname = "";
		// for(int i = 0; i < 10; i++)
		// botData.nickname += alphas[random.nextInt(alphas.length)];
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
			this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(
					proxy, port));
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
			this.loginProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					loginProxy, port));
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
		System.setProperty("socksProxyHost", "");
		System.setProperty("socksProxyPort", "");
		System.out.println("[" + username + "] Connecting...");
		bot = new MinecraftBot(darkBot, botData);
		bot.setMovementDisabled(true);
		connectionHandler = bot.getConnectionHandler();
		Session session = bot.getSession();
		// System.gc();
		System.out.println("[" + username + "] Done! ("
				+ amountJoined.incrementAndGet() + ")");
		bot.getEventManager().registerListener(this);
		bot.getGameHandler().registerListener(this);

		long lastShoutTime = System.currentTimeMillis();
		while(bot.isConnected()) {
			if(die) {
				connectionHandler.sendPacket(new Packet255KickDisconnect(
						"Goodbye"));
				return;
			}
			try {
				Thread.sleep(3000 + random.nextInt(1000));
			} catch(InterruptedException exception) {
				exception.printStackTrace();
			}
			if(!bot.hasSpawned())
				continue;
			connectionHandler
					.sendPacket(new Packet0KeepAlive(random.nextInt()));
			if(spamMessage == null || !canSpam)
				continue;
			String message = spamMessage;
			if(message.contains("%skill"))
				message = message.replace("%skill", skills[nextSkill++]);
			if(nextSkill >= skills.length)
				nextSkill = 0;
			if(message.contains("%bot")) {
				synchronized(bots) {
					message = message.replace("%bot", bots.get(nextBot > bots
							.size() ? (nextBot = 0) * 0 : nextBot++).bot
							.getSession().getUsername());
				}
			}
			if(message.contains("%spamlist"))
				message = message
						.replace("%spamlist", spamList[nextSpamList++]);
			if(nextSpamList >= spamList.length)
				nextSpamList = 0;
			if(message.contains("%rnd")) {
				int length = 1;
				int index = message.indexOf("%rnd") + "%rnd".length();
				int lastIndex;
				for(lastIndex = index; lastIndex < message.length(); lastIndex++)
					if(Character.isDigit(message.charAt(lastIndex)))
						lastIndex++;
					else
						break;
				if(lastIndex > message.length())
					lastIndex--;
				try {
					System.out.println(index + "," + lastIndex + ","
							+ message.length());
					length = Integer.parseInt(message.substring(index,
							lastIndex));
				} catch(Exception exception) {}

				String randomChars = "";
				for(int i = 0; i < length; i++)
					randomChars += alphas[random.nextInt(alphas.length)];
				message = message.replace("%rnd", randomChars);
			}
			if(message.contains("%msg"))
				message = "/msg " + msgChars[nextMsgChar++] + " "
						+ message.replace("%msg", "");
			if(message.contains("%ernd")) {
				message = message.replace("%ernd", "");
				int extraMessageLength = 15 + random.nextInt(6);
				message = message.substring(0,
						Math.min(100 - extraMessageLength, message.length()))
						+ " [";
				extraMessageLength -= 3;
				for(int i = 0; i < extraMessageLength; i++)
					message += alphas[random.nextInt(alphas.length)];
				message += "]";
			} else
				message = message.substring(0, Math.min(100, message.length()));
			connectionHandler.sendPacket(new Packet3Chat(message));
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
			connectionHandler.sendPacket(new Packet0KeepAlive(new Random()
					.nextInt()));
			break;
		case 3:
			String message = ((Packet3Chat) packet).message;
			message = removeColors(message);
			System.out.println("[" + bot.getSession().getUsername() + "] "
					+ message);
			String testMessage = "[MineCaptcha] To be unmuted answer this question: What is ";
			String testMessage2 = "Please type '";
			String testMessage3 = "' to continue sending messages/commands";
			if(message.contains(testMessage)) {
				try {
					String captcha = message.split(Pattern.quote(testMessage))[1]
							.split("[ \\?]")[0];
					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine engine = mgr.getEngineByName("JavaScript");
					String solved = engine.eval(captcha).toString();
					solved = solved.split("\\.")[0];
					connectionHandler.sendPacket(new Packet3Chat(solved));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} else if(message.contains(testMessage2)
					&& message.contains(testMessage3)) {
				try {
					String captcha = message.split(Pattern.quote(testMessage2))[1]
							.split(Pattern.quote(testMessage3))[0];
					connectionHandler.sendPacket(new Packet3Chat(captcha));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} else if(message.startsWith("Please register with \"/register")) {
				String password = "";
				for(int i = 0; i < 10 + random.nextInt(6); i++)
					password += alphas[random.nextInt(alphas.length)];
				bot.say("/register " + password + " " + password);
			} else if(message.startsWith("/uc ")) {
				connectionHandler.sendPacket(new Packet3Chat(message));
			} else if((message.contains("do the crime") && message
					.contains("do the time"))
					|| message.contains("You have been muted")) {
				connectionHandler.sendPacket(new Packet3Chat("\247Leaving!"));
			} else if(message.contains(bot.getOwner()
					+ " has requested to teleport to you.")) {
				connectionHandler.sendPacket(new Packet3Chat("/tpaccept"));
			} else if(message.contains(bot.getOwner())) {
				if(message.contains("Go ")) {
					spamMessage = message.substring(message.indexOf("Go ")
							+ "Go ".length());
				} else if(message.contains("Stop")) {
					spamMessage = null;
					bot.getTaskManager().stopAll();
				} else if(message.contains("Die")) {
					die = true;
				} else if(message.contains("Say ")) {
					connectionHandler.sendPacket(new Packet3Chat(message
							.substring(message.indexOf("Say ")
									+ "Say ".length())));
				} else if(message.contains("Leave")) {
					connectionHandler.sendPacket(new Packet255KickDisconnect(
							"Quit"));
				} else if(message.contains("Tool")) {
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
				} else if(message.contains("Owner ")) {
					String name = message.substring(
							message.indexOf("Owner ") + "Owner ".length())
							.split(" ")[0];
					bot.setOwner(name);
					bot.say("Set owner to " + name);
				}
			} else if(message.contains("You are not member of any faction.")
					&& spamMessage != null && createFaction) {
				String msg = "/f create ";
				for(int i = 0; i < 7 + random.nextInt(3); i++)
					msg += alphas[random.nextInt(alphas.length)];
				bot.say(msg);
			}
			if(message.matches("[\\*]*SPQR [\\w]{1,16} invited you to SPQR")) {
				bot.say("/f join SPQR");
				bot.say("\247asdf");
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
		}
	}

	// @EventHandler
	// public void onPacketSent(PacketSentEvent event) {
	// System.out.println("Packet sent: " + event.getPacket().getId() + " ("
	// + event.getPacket() + ")");
	// }

	private String removeColors(final String input) {
		if(input != null)
			return input.replaceAll("(?i)§[0-F]", "");
		return null;
	}

	public MinecraftBot getBot() {
		return bot;
	}

	@EventHandler
	public void onSpawn(SpawnEvent event) {
		bot.getPlayer().getInventory().setDelay(500);
		// bot.say("/msg GrimReaperV1 Invite me!@#");
		// // String password = "";
		// // for(int i = 0; i < 10 + random.nextInt(6); i++)
		// // password += alphas[random.nextInt(alphas.length)];
		// // bot.say("/register " + password + " " + password);
		// if(spamMessage != null && createFaction) {
		// String msg = "/f create ";
		// for(int i = 0; i < 7 + random.nextInt(3); i++)
		// msg += alphas[random.nextInt(alphas.length)];
		// bot.say(msg);
		// }
		// bot.getConnectionHandler().sendPacket(
		// new Packet255KickDisconnect("Quit"));
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		System.out.println("[" + bot.getSession().getUsername()
				+ "] Disconnected: " + event.getReason());
		bot.getService().shutdownNow();
	}

	double distanceToNearestLog = Double.MAX_VALUE;

	boolean firstStart = true, asdfasdf = true, asdfasdfasdf = true;
	int ticksToGo = 250;
	boolean canSpam = false;

	double lastYaw, lastPitch;
	int ticksLeftTillMove = 100;

	BlockLocation spawn = new BlockLocation(287, 69, 291);
	int mode = 0;

	// BlockLocation spawn = new BlockLocation(373, 73, 583);

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
		if(firstStart) {
			bot.say("/kit starter");
			ticksToGo = 15;
			firstStart = false;
			return;
		} else if(asdfasdfasdf) {
			bot.say("/warp dinfo");
			ticksToGo = 200;
			asdfasdfasdf = false;
			return;
		} else if(asdfasdf) {
			bot.say("/pay DarkStorm_ 500");
			try {
				PlayerInventory inventory = player.getInventory();
				for(int i = 0; i < 44; i++) {
					ItemStack item = inventory.getItemAt(i);
					if(item != null) {
						inventory.selectItemAt(i);
						inventory.dropSelectedItem();
					}
				}
			} catch(Exception exception) {}
			bot.say("\247bai");
			asdfasdf = false;
			return;
		}
		// connectionHandler.disconnect("");
		// return;
		// } else if("".equals(""))
		// return;
		// if(player.getDistanceTo(spawn) < 7 && player.getZ() < 589.5) {
		// player.setZ(player.getZ() + 0.12);
		// bot.updateMovement();
		// } else
		// canSpam = true;

		// if(player.getDistanceTo(spawn) < 10 && player.getY() < 72) {
		// if(player.getZ() < -37.6)
		// player.setZ(player.getZ() + 0.1);
		// else if(player.getX() < 232.4)
		// player.setX(player.getX() + 0.1);
		// else
		// player.setY(player.getY() + 0.1);
		// bot.updateMovement();
		// } else if(player.getZ() > -38 && player.getZ() < -36.6
		// && player.getX() > 232 && player.getX() < 233) {
		// player.setZ(player.getZ() + 0.1);
		// bot.updateMovement();
		// } else

		// if(player.getDistanceToSquared(spawn) > 49)
		// mode = 3;
		// if(mode == 0)
		// mode = player.getZ() > 295 ? 2 : 1;
		// switch(mode) {
		// case -1:
		// canSpam = true;
		// break;
		// case 1:
		// canSpam = false;
		// if(player.getZ() < 296.3) {
		// player.setZ(player.getZ() + 0.1);
		// bot.updateMovement();
		// } else
		// mode = -1;
		// break;
		// case 2:
		// canSpam = false;
		// if(player.getZ() > 286.6) {
		// player.setZ(player.getZ() - 0.1);
		// bot.updateMovement();
		// } else
		// mode = -1;
		// break;
		// default:
		// connectionHandler.disconnect("Bad location!");
		// break;
		// }

		canSpam = true;
	}

	private DarkBotMCSpambot getClosest() {
		double closestDistance = Double.MAX_VALUE;
		DarkBotMCSpambot closestBot = null;
		synchronized(bots) {
			for(DarkBotMCSpambot bot : bots) {
				if(!bot.getBot().hasSpawned())
					continue;
				double distance = bot.getBot().getPlayer()
						.getDistanceToSquared(bot.getBot().getPlayer());
				if(distance < closestDistance) {
					distance = closestDistance;
					closestBot = bot;
				}
			}
		}
		return closestBot;
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
		OptionSpec<?> offlineOption = parser
				.acceptsAll(
						Arrays.asList("O", "offline"),
						"Offline-mode. Ignores 'password' and 'account-list' (will "
								+ "generate random usernames if 'username' is not supplied).");
		OptionSpec<?> autoRejoinOption = parser.acceptsAll(
				Arrays.asList("a", "auto-rejoin"),
				"Auto-rejoin a server on disconnect.");
		OptionSpec<Integer> loginDelayOption = parser
				.acceptsAll(
						Arrays.asList("d", "login-delay"),
						"Delay between bot joins, in milliseconds. 5000 is "
								+ "recommended if not using socks proxies.")
				.withRequiredArg().describedAs("delay").ofType(Integer.class);
		OptionSpec<Integer> botAmountOption = parser
				.acceptsAll(Arrays.asList("b", "bot-amount"),
						"Amount of bots to join. Must be <= amount of accounts.")
				.withRequiredArg().describedAs("amount").ofType(Integer.class);

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
		if(options.has(accountListOption)) {
			accounts = loadAccounts(options.valueOf(accountListOption));
		} else if(!offline) {
			System.out.println("Option 'accounts' must be supplied in "
					+ "absence of option 'offline'.");
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
			System.out.println("Option 'http-proxy-list' required if "
					+ "option 'account-list' is supplied.");
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

		ExecutorService service = Executors.newFixedThreadPool(botAmount
				+ (loginDelay > 0 ? 1 : 0));
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
							String account = accounts.get(random
									.nextInt(accounts.size()));
							synchronized(accountsInUse) {
								if(accountsInUse.size() == accounts.size())
									System.exit(0);
								while(accountsInUse.contains(account))
									account = accounts.get(random
											.nextInt(accounts.size()));
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
								loginProxy = httpProxies.get(random
										.nextInt(httpProxies.size()));
								try {
									session = Util.retrieveSession(
											accountParts[0], accountParts[1],
											loginProxy);
									// addAccount(session);
									sessionCount.incrementAndGet();
									authenticated = true;
									break;
								} catch(AuthenticationException exception) {
									System.err.println("[Bot" + botNumber
											+ "] " + exception);
									if(!exception.getMessage().startsWith(
											"Exception"))
										// && !exception.getMessage().equals(
										// "Too many failed logins"))
										continue user;
								}
							}
							System.out
									.println("[" + session.getUsername()
											+ "] Password: "
											+ session.getPassword()
											+ ", Session ID: "
											+ session.getSessionId());
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
								String proxy = useProxy ? socksProxies
										.get(random.nextInt(socksProxies.size()))
										: null;
								try {
									new DarkBotMCSpambot(DARK_BOT, server,
											session.getUsername(),
											session.getPassword(),
											session.getSessionId(), null,
											proxy, owner);
									if(die)
										break user;
									else if(!autoRejoin)
										break;
								} catch(Exception exception) {
									exception.printStackTrace();
									System.out.println("["
											+ session.getUsername()
											+ "] Error connecting: "
											+ exception.getCause().toString());
								}
							}
							System.out.println("[" + session.getUsername()
									+ "] Account failed");
						}
					} else {
						while(true) {
							String proxy = useProxy ? socksProxies.get(random
									.nextInt(socksProxies.size())) : null;
							try {
								String username = "";
								if(accounts != null) {
									username = accounts.get(
											random.nextInt(accounts.size()))
											.split(":")[0];
									synchronized(accountsInUse) {
										while(accountsInUse.contains(username))
											username = accounts.get(random
													.nextInt(accounts.size()));
										accountsInUse.add(username);
									}
								} else
									for(int i = 0; i < 10 + random.nextInt(6); i++)
										username += alphas[random
												.nextInt(alphas.length)];
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
								new DarkBotMCSpambot(DARK_BOT, server,
										username, "", "", null, proxy, owner);
								if(die || !autoRejoin)
									break;
								else
									continue;
							} catch(Exception exception) {
								System.out.println("[Bot" + botNumber
										+ "] Error connecting: "
										+ exception.toString());
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
		frame.add(sessionsButton, new GridBagConstraints(0, 0, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets,
				0, 0));
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
		frame.add(joinsButton, new GridBagConstraints(0, 1, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets,
				0, 0));
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
		frame.add(field, new GridBagConstraints(0, 2, 1, 1, 1, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets,
				0, 0));
		final JButton button = new JButton("Start");
		frame.add(button, new GridBagConstraints(1, 2, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, noInsets,
				0, 0));
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
				sessionsButton.setText(sessionsButton.getText().split(" ")[0]
						+ " (" + Integer.toString(sessionCount.get()) + ")");
				joinsButton.setText(joinsButton.getText().split(" ")[0] + " ("
						+ Integer.toString(amountJoined.get()) + ")");
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