package org.darkstorm.darkbot.darkbotmc.regular;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;

import javax.naming.AuthenticationException;

import org.darkstorm.darkbot.darkbotmc.DarkBotMC;
import org.darkstorm.darkbot.darkbotmc.regular.commands.*;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet3Chat;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.*;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet205ClientCommand;
import org.darkstorm.darkbot.minecraftbot.util.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData.ProxyType;

public class RegularBot implements EventListener {
	private final RegularBotControlsUI ui;
	private final ExecutorService service = Executors.newCachedThreadPool();
	private final RegularBotData data;
	private final List<Command> commands;
	private MinecraftBot bot;

	private boolean awaitingSpawn = false, awaitingStatus = false,
			awaitingInventory = false;

	public RegularBot(RegularBotControlsUI ui, RegularBotData data) {
		this.ui = ui;
		data.lock();
		this.data = data;
		commands = new ArrayList<Command>();
		commands.addAll(Arrays.asList((Command[]) DefaultCommands.values()));
		status("Waiting.");
		progress(0, false);
		connect();
	}

	public void connect() {
		service.execute(new Runnable() {
			@Override
			public void run() {
				MinecraftBotData.Builder builder = MinecraftBotData.builder();
				builder.withUsername(data.username);
				builder.withPassword(data.password);

				String server = data.server;
				int port = 25565;
				if(server.contains(":")) {
					String[] parts = server.split(":");
					server = parts[0];
					port = Integer.parseInt(parts[1]);
				}
				builder.withServer(server);
				builder.withPort(port);

				if(data.proxy != null) {
					String proxy = data.proxy;
					int proxyPort;
					ProxyType type = null;
					if(proxy.contains(":")) {
						String[] parts = proxy.split(":");
						proxy = parts[0];
						proxyPort = Integer.parseInt(parts[1]);
						if(parts.length > 2) {
							try {
								type = ProxyType.values()[Integer
										.parseInt(parts[2])];
							} catch(NumberFormatException exception) {
								type = ProxyType.valueOf(parts[2].toUpperCase());
							}
						}
					} else
						throw new IllegalArgumentException("Invalid proxy");
					ProxyData data = new ProxyData(proxy, proxyPort,
							type == null ? ProxyType.SOCKS : type);
					builder.withSocksProxy(data);
				}
				MinecraftBotData botData = builder.build();

				clearLog();
				log("[BOT] Connecting...");
				status("Connecting...");
				progress(true);
				try {
					bot = new MinecraftBot(
							DarkBotMC.getInstance().getDarkBot(), botData);
				} catch(Exception exception) {
					exception.printStackTrace();

					Throwable cause = exception.getCause();
					if(cause != null
							&& cause instanceof AuthenticationException) {
						log("[BOT] Error: Invalid login (" + cause.getMessage()
								+ ")");
					} else {
						log("[BOT] Error: Unable to connect ("
								+ exception.toString() + ")");
					}
					status("Waiting.");
					progress(false);
					return;
				}
				progress(20, false);
				status("Logging in...");
				bot.getEventManager().registerListener(RegularBot.this);
				TaskManager taskManager = bot.getTaskManager();
				for(Class<? extends Task> task : data.tasks) {
					try {
						Constructor<? extends Task> constructor = task
								.getConstructor(MinecraftBot.class);
						taskManager.registerTask(constructor.newInstance(bot));
					} catch(Exception exception) {}
				}
			}
		});
	}

	public void disconnect() {
		if(bot != null) {
			System.out.println("Disconnected");
			bot.getConnectionHandler().disconnect("");
			bot.getEventManager().unregisterListener(this);
			bot = null;
			awaitingSpawn = awaitingStatus = false;
			status("Waiting.");
			progress(0, false);
		}
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		String reason = event.getReason();
		log("[BOT] Disconnected"
				+ (reason != null && reason.length() > 0 ? ": " + reason : "")
				+ ".");
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		switch(packet.getId()) {
		case 1:
			awaitingSpawn = true;
			status("Loading...");
			progress(40);
			break;
		case 3:
			Packet3Chat chatPacket = (Packet3Chat) packet;
			log("[CHAT] " + chatPacket.message);
			break;
		case 13:
			if(awaitingSpawn) {
				awaitingSpawn = false;
				awaitingInventory = true;
				progress(60);
			}
			break;
		case 8:
			if(((Packet8UpdateHealth) packet).healthMP <= 0)
				bot.getConnectionHandler().sendPacket(
						new Packet205ClientCommand(1));
			ui.updateStatus();
			if(awaitingStatus) {
				awaitingStatus = false;
				status("Connected.");
				progress(100);
			}
			break;
		case 104:
			Packet104WindowItems itemsPacket = (Packet104WindowItems) packet;
			if(awaitingInventory && itemsPacket.windowId == 0) {
				awaitingInventory = false;
				awaitingStatus = true;
				progress(80);
			}
		}
	}

	public void clearLog() {
		ui.clearLog();
	}

	public void log(String text) {
		ui.log(text);
	}

	public void status(String status) {
		ui.setStatus(status);
	}

	public void progress(int percentage) {
		ui.setProgress(percentage);
	}

	public void progress(boolean indeterminate) {
		ui.setProgress(indeterminate);
	}

	public void progress(int percentage, boolean indeterminate) {
		ui.setProgress(percentage, indeterminate);
	}

	public void executeCommand(String commandText) {
		String[] parts = commandText.split(" ");
		String commandName = parts[0];
		String[] args = new String[parts.length - 1];
		for(int i = 1; i < parts.length; i++)
			args[i - 1] = parts[i];

		Command targetCommand = null;
		synchronized(commands) {
			for(Command command : commands)
				if(commandName.equalsIgnoreCase(command.getName()))
					targetCommand = command;
		}

		if(targetCommand == null)
			log("[BOT] Unknown command.");
		else if(!targetCommand.execute(this, args))
			log("[BOT] Invalid command usage.");
	}

	public MinecraftBot getBot() {
		return bot;
	}

	public RegularBotData getData() {
		return data;
	}

	public List<Command> getCommands() {
		synchronized(commands) {
			return Collections.unmodifiableList(commands);
		}
	}

	public static class RegularBotData {
		private String username, password, server, proxy;
		private List<Class<? extends Task>> tasks;

		private boolean locked = false;

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public String getServer() {
			return server;
		}

		public String getProxy() {
			return proxy;
		}

		public List<Class<? extends Task>> getTasks() {
			return tasks;
		}

		public synchronized void setUsername(String username) {
			if(locked)
				throw new UnsupportedOperationException();
			this.username = username;
		}

		public synchronized void setPassword(String password) {
			if(locked)
				throw new UnsupportedOperationException();
			this.password = password;
		}

		public synchronized void setServer(String server) {
			if(locked)
				throw new UnsupportedOperationException();
			this.server = server;
		}

		public synchronized void setProxy(String proxy) {
			if(locked)
				throw new UnsupportedOperationException();
			this.proxy = proxy;
		}

		public synchronized void setTasks(List<Class<? extends Task>> tasks) {
			if(locked)
				throw new UnsupportedOperationException();
			if(tasks == null)
				throw new NullPointerException();
			this.tasks = Collections.unmodifiableList(tasks);
		}

		private synchronized void lock() {
			locked = true;
		}
	}
}
