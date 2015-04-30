package org.darkstorm.darkbot.mcwrapper.gui.regular;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;

import javax.naming.AuthenticationException;

import org.darkstorm.darkbot.mcwrapper.gui.regular.commands.*;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.event.*;
import org.darkstorm.darkbot.minecraftbot.event.EventListener;
import org.darkstorm.darkbot.minecraftbot.event.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.event.protocol.client.RequestRespawnEvent;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.*;
import org.darkstorm.darkbot.minecraftbot.util.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData.ProxyType;

public class RegularBot implements EventListener {
	private final RegularBotControlsUI ui;
	private final ExecutorService service = Executors.newCachedThreadPool();
	private final RegularBotData data;
	private final List<Command> commands;
	private MinecraftBot bot;

	private int loadingState = 0;

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
				MinecraftBot.Builder builder = MinecraftBot.builder();
				builder.username(data.username).password(data.password);

				String server = data.server;
				int port = 25565;
				if(server.contains(":")) {
					String[] parts = server.split(":");
					server = parts[0];
					port = Integer.parseInt(parts[1]);
				}
				builder.server(server).port(port);

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
								type = ProxyType.values()[Integer.parseInt(parts[2])];
							} catch(NumberFormatException exception) {
								type = ProxyType.valueOf(parts[2].toUpperCase());
							}
						}
					} else
						throw new IllegalArgumentException("Invalid proxy");
					ProxyData data = new ProxyData(proxy, proxyPort, type == null ? ProxyType.SOCKS : type);
					builder.connectProxy(data);
				}

				clearLog();
				log("[BOT] Connecting...");
				status("Connecting...");
				progress(true);
				try {
					bot = builder.build();
				} catch(Exception exception) {
					exception.printStackTrace();

					Throwable cause = exception.getCause();
					if(cause != null && cause instanceof AuthenticationException) {
						log("[BOT] Error: Invalid login (" + cause.getMessage() + ")");
					} else {
						log("[BOT] Error: Unable to connect (" + exception.toString() + ")");
					}
					status("Waiting.");
					progress(false);
					return;
				}
				progress(20, false);
				status("Logging in...");
				bot.getEventBus().register(RegularBot.this);
				TaskManager taskManager = bot.getTaskManager();
				for(Class<? extends Task> task : data.tasks) {
					try {
						Constructor<? extends Task> constructor = task.getConstructor(MinecraftBot.class);
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
			bot.getEventBus().unregister(this);
			bot = null;
			loadingState = 0;
			status("Waiting.");
			progress(0, false);
		}
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		String reason = event.getReason();
		log("[BOT] Disconnected" + (reason != null && reason.length() > 0 ? ": " + reason : "") + ".");
	}

	@EventHandler
	public void onChatReceived(ChatReceivedEvent event) {
		log("[CHAT] " + event.getMessage());
	}

	@EventHandler
	public void onLogin(LoginEvent event) {
		status("Loading...");
		progress(40);
		loadingState = 1;
	}

	@EventHandler
	public void onTeleport(TeleportEvent event) {
		if(loadingState == 1) {
			progress(60);
			loadingState = 2;
		}
	}

	@EventHandler
	public void onWindowUpdate(WindowUpdateEvent event) {
		if(loadingState == 2 && event.getWindowId() == 0) {
			loadingState = 3;
			progress(80);
		}
	}

	@EventHandler
	public void onHealthUpdate(HealthUpdateEvent event) {
		ui.updateStatus();
		if(loadingState == 3) {
			loadingState = 4;
			status("Connected.");
			progress(100);
		}
		if(event.getHealth() <= 0)
			bot.getEventBus().fire(new RequestRespawnEvent());
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
