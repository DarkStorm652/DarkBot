package org.darkstorm.darkbot.darkbotmc.spam;

import java.util.*;

import javax.naming.AuthenticationException;

import org.darkstorm.darkbot.darkbotmc.DarkBotMC;
import org.darkstorm.darkbot.darkbotmc.spam.ActionProvider.Action;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.*;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet205ClientCommand;

public class SpamBot implements EventListener {
	private final SpamBotControlsUI ui;
	private final SpamBotData data;
	private MinecraftBot bot;

	private ActionManager manager;

	private boolean awaitingSpawn = false, awaitingStatus = false,
			awaitingInventory = false;

	public SpamBot(SpamBotControlsUI ui, SpamBotData data) {
		this.ui = ui;
		this.data = data;
		data.lock();
		manager = new BasicActionManager(bot);
		manager.setActions(data.getActions().toArray(
				new Action[data.getActions().size()]));
		status("Waiting.");
		progress(0, false);
		connect();
	}

	public void connect() {
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

		// if(data.proxy != null) {
		// String proxy = data.proxy;
		// int proxyPort;
		// if(proxy.contains(":")) {
		// String[] parts = proxy.split(":");
		// proxy = parts[0];
		// proxyPort = Integer.parseInt(parts[1]);
		// } else
		// throw new IllegalArgumentException("Invalid proxy");
		// botData.proxy = proxy;
		// botData.proxyPort = proxyPort;
		// }
		MinecraftBotData botData = builder.build();

		status("Connecting...");
		progress(true);
		try {
			bot = new MinecraftBot(DarkBotMC.getInstance().getDarkBot(),
					botData);
		} catch(Exception exception) {
			exception.printStackTrace();

			Throwable cause = exception.getCause();
			if(cause != null && cause instanceof AuthenticationException) {
				// log("[BOT] Error: Invalid login (" +
				// cause.getMessage()
				// + ")");
			} else {
				status("red", "Unable to connect: " + exception.toString());
			}
			status("red", "Error: " + exception.toString());
			progress(0, false);
			return;
		}
		progress(20, false);
		status("yellow", "Logging in...");
		bot.getEventManager().registerListener(SpamBot.this);
		// TaskManager taskManager = bot.getTaskManager();
		// for(Class<? extends Task> task : data.tasks) {
		// try {
		// Constructor<? extends Task> constructor = task
		// .getConstructor(MinecraftBot.class);
		// taskManager.registerTask(constructor.newInstance(bot));
		// } catch(Exception exception) {}
		// }
	}

	public void disconnect() {
		if(bot != null) {
			bot.getConnectionHandler().disconnect("");
			bot.getEventManager().unregisterListener(this);
			bot = null;
			awaitingSpawn = awaitingStatus = false;
			status("red", "Disconnected.");
			progress(0, false);
		}
	}

	public void performAction(String action) {

	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		// String reason = event.getReason();
		// log("[BOT] Disconnected"
		// + (reason != null && reason.length() > 0 ? ": " + reason : "")
		// + ".");
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		switch(packet.getId()) {
		case 1:
			awaitingSpawn = true;
			status("yellow", "Loading...");
			progress(40);
			break;
		case 3:
			// Packet3Chat chatPacket = (Packet3Chat) packet;
			// log("[CHAT] " + chatPacket.message);
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
			// ui.updateStatus();
			if(awaitingStatus) {
				awaitingStatus = false;
				status("green", "Connected.");
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
