package org.darkstorm.minecraft.darkbot.wrapper.gui.spam;

import java.util.*;

import javax.naming.AuthenticationException;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.event.general.DisconnectEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.client.RequestRespawnEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.server.*;
import org.darkstorm.minecraft.darkbot.wrapper.gui.spam.ActionProvider.Action;

public class SpamBot implements EventListener {
	private final SpamBotControlsUI ui;
	private final SpamBotData data;
	private MinecraftBot bot;

	private ActionManager manager;

	private int loadingState = 0;

	public SpamBot(SpamBotControlsUI ui, SpamBotData data) {
		this.ui = ui;
		this.data = data;
		data.lock();
		manager = new BasicActionManager(bot);
		manager.setActions(data.getActions().toArray(new Action[data.getActions().size()]));
		status("Waiting.");
		progress(0, false);
		connect();
	}

	public void connect() {
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

		/*if(data.proxy != null) {
			String proxy = data.proxy;
			int proxyPort;
			if(proxy.contains(":")) {
				String[] parts = proxy.split(":");
				proxy = parts[0];
				proxyPort = Integer.parseInt(parts[1]);
			} else
				throw new IllegalArgumentException("Invalid proxy");
			botData.proxy = proxy;
			botData.proxyPort = proxyPort;
		}*/

		status("Connecting...");
		progress(true);
		try {
			bot = builder.build();
		} catch(Exception exception) {
			exception.printStackTrace();

			Throwable cause = exception.getCause();
			if(cause != null && cause instanceof AuthenticationException) {
				// log("[BOT] Error: Invalid login (" + cause.getMessage() +
				// ")");
			} else {
				status("red", "Unable to connect: " + exception.toString());
			}
			status("red", "Error: " + exception.toString());
			progress(0, false);
			return;
		}
		progress(20, false);
		status("yellow", "Logging in...");
		bot.getEventBus().register(SpamBot.this);
		/*TaskManager taskManager = bot.getTaskManager();
		for(Class<? extends Task> task : data.tasks) {
			try {
				Constructor<? extends Task> constructor = task.getConstructor(MinecraftBot.class);
				taskManager.registerTask(constructor.newInstance(bot));
			} catch(Exception exception) {}
		}*/
	}

	public void disconnect() {
		if(bot != null) {
			bot.getConnectionHandler().disconnect("");
			bot.getEventBus().unregister(this);
			bot = null;
			loadingState = 0;
			status("red", "Disconnected.");
			progress(0, false);
		}
	}

	public void performAction(String action) {

	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		// String reason = event.getReason();
		// log("[BOT] Disconnected" + (reason != null && reason.length() > 0 ?
		// ": " + reason : "") + ".");
	}

	@EventHandler
	public void onChatReceived(ChatReceivedEvent event) {
		// log("[CHAT] " + event.getMessage());
	}

	@EventHandler
	public void onLogin(LoginEvent event) {
		status("yellow", "Loading...");
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
		// ui.updateStatus();
		if(loadingState == 3) {
			loadingState = 4;
			status("green", "Connected.");
			progress(100);
		}
		if(event.getHealth() <= 0)
			bot.getEventBus().fire(new RequestRespawnEvent());
	}

	private void status(String color, String status) {
		status("<font color=\"" + color + "\">" + status + "</font>");
	}

	private void status(String status) {
		ui.setStatus(this, status);
	}

	private void progress(int percentage) {
		ui.setProgress(this, percentage);
	}

	private void progress(boolean indeterminate) {
		ui.setProgress(this, indeterminate);
	}

	private void progress(int percentage, boolean indeterminate) {
		ui.setProgress(this, percentage, indeterminate);
	}

	public void executeCommand(String command) {

	}

	public MinecraftBot getBot() {
		return bot;
	}

	public SpamBotData getData() {
		return data;
	}

	static class SpamBotData {
		private String username, password, server;
		private int botAmount, loginDelay;
		private List<String> proxies;
		private List<Action> actions;

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

		public int getBotAmount() {
			return botAmount;
		}

		public int getLoginDelay() {
			return loginDelay;
		}

		public List<String> getProxies() {
			return proxies;
		}

		public List<Action> getActions() {
			return actions;
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

		public synchronized void setBotAmount(int botAmount) {
			if(locked)
				throw new UnsupportedOperationException();
			this.botAmount = botAmount;
		}

		public synchronized void setLoginDelay(int loginDelay) {
			if(locked)
				throw new UnsupportedOperationException();
			this.loginDelay = loginDelay;
		}

		public synchronized void setProxies(List<String> proxies) {
			if(locked)
				throw new UnsupportedOperationException();
			if(proxies == null)
				throw new NullPointerException();
			this.proxies = Collections.unmodifiableList(proxies);
		}

		public synchronized void setActions(List<Action> actions) {
			if(locked)
				throw new UnsupportedOperationException();
			if(actions == null)
				throw new NullPointerException();
			this.actions = Collections.unmodifiableList(actions);
		}

		private synchronized void lock() {
			locked = true;
		}
	}
}
