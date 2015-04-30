package org.darkstorm.darkbot.minecraftbot;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

import org.apache.commons.lang3.StringUtils;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.auth.*;
import org.darkstorm.darkbot.minecraftbot.event.*;
import org.darkstorm.darkbot.minecraftbot.event.general.*;
import org.darkstorm.darkbot.minecraftbot.event.protocol.client.*;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.*;
import org.darkstorm.darkbot.minecraftbot.event.world.SpawnEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.util.*;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class MinecraftBot implements EventListener {
	public static final int DEFAULT_PORT = 25565;
	public static final int LATEST_PROTOCOL = -1;
	public static final int MAX_CHAT_LENGTH = 100;
	public static final UUID CLIENT_TOKEN = Util.generateSystemUUID("CLIENT_TOKEN");

	private final ExecutorService service;
	private final EventBus eventBus;
	private final TaskManager taskManager;
	private final ConnectionHandler connectionHandler;
	private final AuthService<?> authService;
	private final Session session;
	private final ProxyData loginProxy, connectProxy;

	private MainPlayerEntity player;
	private World world;

	private double lastX, lastY, lastZ, lastYaw, lastPitch;
	private boolean hasSpawned, movementDisabled, muted;
	private int messageDelay, inventoryDelay;
	private long lastMessage;
	private Activity activity;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MinecraftBot(Builder builder) throws AuthenticationException, UnsupportedProtocolException, InvalidSessionException, IOException {
		service = Executors.newCachedThreadPool();
		eventBus = new ConcurrentEventBus();
		eventBus.register(this);
		taskManager = new BasicTaskManager(this);

		Protocol<?> protocol;
		if(builder.getProtocol() >= 0) {
			ProtocolProvider<?> provider = ProtocolProvider.getProvider(builder.getProtocol());
			if(provider == null)
				throw new UnsupportedProtocolException("No protocol support for v" + builder.getProtocol() + " found.");
			protocol = provider.getProtocolInstance(this);
		} else
			protocol = ProtocolProvider.getLatestProvider().getProtocolInstance(this);
		connectionHandler = new SocketConnectionHandler(this, protocol, builder.getServer(), builder.getPort(), builder.getConnectProxy());

		if(builder.getAuthService() != null)
			authService = builder.getAuthService();
		else if(protocol instanceof ProtocolX)
			authService = new YggdrasilAuthService(CLIENT_TOKEN);
		else
			authService = new LegacyAuthService();

		if(builder.getSession() != null)
			authService.validateSession(builder.getSession());
		loginProxy = builder.getLoginProxy();
		connectProxy = builder.getConnectProxy();

		if(StringUtils.isNotBlank(builder.getPassword()) && (builder.getSession() == null || !builder.getSession().isValidForAuthentication()))
			session = authService.login(builder.getUsername(), builder.getPassword(), loginProxy);
		else if(builder.getSession() != null)
			session = builder.getSession();
		else
			session = new OfflineSession(builder.getUsername());

		connectionHandler.connect();

		eventBus.fire(new HandshakeEvent(session, connectionHandler.getServer(), connectionHandler.getPort()));
		new TickHandler();
	}

	@EventHandler
	public void onLogin(LoginEvent event) {
		setWorld(new BasicWorld(this, event.getWorldType(), event.getDimension(), event.getDifficulty(), event.getWorldHeight()));
		player = new MainPlayerEntity(world, event.getPlayerId(), session.getUsername(), event.getGameMode());
		world.spawnEntity(player);
	}

	@EventHandler
	public void onRespawn(RespawnEvent event) {
		setWorld(new BasicWorld(this, event.getWorldType(), event.getRespawnDimension(), event.getDifficulty(), event.getWorldHeight()));
		player.setGameMode(event.getGameMode());
	}

	@EventHandler
	public void onTeleport(TeleportEvent event) {
		player.setX(event.getX());
		player.setY(event.getY());
		player.setZ(event.getZ());
		player.setYaw(event.getYaw());
		player.setPitch(event.getPitch());
		if(!hasSpawned) {
			eventBus.fire(new SpawnEvent(player));
			hasSpawned = true;
		}
		player.setVelocityX(0);
		player.setVelocityY(0.12);
		player.setVelocityZ(0);
	}

	@EventHandler
	public void onHealthUpdate(HealthUpdateEvent event) {
		player.setHealth(event.getHealth());
		player.setHunger(event.getHunger());
	}

	@EventHandler
	public void onExperienceUpdate(ExperienceUpdateEvent event) {
		player.setExperienceLevel(event.getExperienceLevel());
		player.setExperienceTotal(event.getExperienceTotal());
	}

	@EventHandler
	public void onWindowOpen(WindowOpenEvent event) {
		if(player == null)
			return;
		System.out.println("Opened inventory " + event.getInventoryType() + ": " + event.getSlotCount() + " slots");
		switch(event.getInventoryType()) {
		case CHEST:
			player.setWindow(new ChestInventory(this, event.getWindowId(), event.getSlotCount() == 27 ? false : true));
			break;
		default:
		}
	}

	@EventHandler
	public void onWindowClose(WindowCloseEvent event) {
		if(player == null)
			return;
		player.setWindow(null);
	}

	@EventHandler
	public void onWindowSlotChange(WindowSlotChangeEvent event) {
		if(player == null)
			return;
		Inventory window = player.getWindow();
		if(event.getWindowId() != 0 && (window == null || event.getWindowId() != window.getWindowId()))
			return;
		if(event.getWindowId() == 0)
			player.getInventory().setItemFromServerAt(event.getSlot(), event.getNewItem());
		else
			window.setItemFromServerAt(event.getSlot(), event.getNewItem());
	}

	@EventHandler
	public void onWindowUpdate(WindowUpdateEvent event) {
		if(player == null)
			return;
		Inventory window = player.getWindow();
		if(event.getWindowId() != 0 && (window == null || event.getWindowId() != window.getWindowId()))
			return;
		ItemStack[] items = event.getItems();
		if(event.getWindowId() == 0)
			for(int i = 0; i < items.length; i++)
				player.getInventory().setItemFromServerAt(i, items[i]);
		else
			for(int i = 0; i < items.length; i++)
				window.setItemFromServerAt(i, items[i]);
	}

	@EventHandler
	public void onKick(KickEvent event) {
		connectionHandler.disconnect("Kicked: " + event.getReason());
	}

	public synchronized void runTick() {
		try {
			connectionHandler.process();

			if(hasSpawned && !player.getInventory().hasActionsQueued() && (player.getWindow() == null || !player.getWindow().hasActionsQueued())) {
				taskManager.update();
				if(activity != null) {
					if(activity.isActive()) {
						activity.run();
						if(!activity.isActive()) {
							activity.stop();
							activity = null;
						}
					} else {
						activity.stop();
						activity = null;
					}
				}
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		try {
			eventBus.fire(new TickEvent());
			if(hasSpawned && !movementDisabled)
				updateMovement();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public synchronized void updateMovement() {
		double x = player.getX(), y = player.getY(), z = player.getZ(), yaw = player.getYaw(), pitch = player.getPitch();
		boolean move = x != lastX || y != lastY || z != lastZ;
		boolean rotate = yaw != lastYaw || pitch != lastPitch;
		boolean onGround = player.isOnGround();

		PlayerUpdateEvent event;
		if(move && rotate)
			event = new PlayerMoveRotateEvent(player, x, y, z, yaw, pitch, onGround);
		else if(move)
			event = new PlayerMoveEvent(player, x, y, z, onGround);
		else if(rotate)
			event = new PlayerRotateEvent(player, yaw, pitch, onGround);
		else
			event = new PlayerUpdateEvent(player, onGround);
		eventBus.fire(event);

		lastX = x;
		lastY = y;
		lastZ = z;
		lastYaw = yaw;
		lastPitch = pitch;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(player == null)
			return;
		if(event.getInventory().getWindowId() != 0)
			player.setWindow(null);
	}

	@EventHandler
	public synchronized void onDisconnect(DisconnectEvent event) {
		service.shutdownNow();
		eventBus.clearListeners();
		hasSpawned = false;
		player = null;
		world = null;
	}

	public synchronized void say(String message) {
		if(muted)
			return;
		while(message.length() > MAX_CHAT_LENGTH) {
			long elapsed = System.currentTimeMillis() - lastMessage;
			if(elapsed < messageDelay) {
				try {
					Thread.sleep(messageDelay - elapsed);
				} catch(InterruptedException e) {}
			}
			String part = message.substring(0, MAX_CHAT_LENGTH);
			System.out.println("Attempting to say: " + message);
			eventBus.fire(new ChatSentEvent(part));
			message = message.substring(part.length());
			lastMessage = System.currentTimeMillis();
		}
		if(!message.isEmpty()) {
			long elapsed = System.currentTimeMillis() - lastMessage;
			if(elapsed < messageDelay) {
				try {
					Thread.sleep(messageDelay - elapsed);
				} catch(InterruptedException e) {}
			}
			System.out.println("Attempting to say: " + message);
			eventBus.fire(new ChatSentEvent(message));
			lastMessage = System.currentTimeMillis();
		}
	}

	public boolean hasSpawned() {
		return hasSpawned;
	}

	public synchronized World getWorld() {
		return world;
	}

	public synchronized void setWorld(World world) {
		if(this.world != null)
			this.world.destroy();
		this.world = world;
		if(player != null) {
			if(world != null)
				player = new MainPlayerEntity(world, player.getId(), player.getName(), player.getGameMode());
			else
				player = null;
			world.spawnEntity(player);
		}
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		if(activity == null && this.activity != null)
			this.activity.stop();
		this.activity = activity;
	}

	public boolean hasActivity() {
		return activity != null;
	}

	public Session getSession() {
		return session;
	}

	public ExecutorService getService() {
		return service;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public AuthService<?> getAuthService() {
		return authService;
	}

	public ProxyData getLoginProxy() {
		return loginProxy;
	}

	public ProxyData getConnectProxy() {
		return connectProxy;
	}

	public boolean isConnected() {
		return connectionHandler.isConnected();
	}

	public boolean isMovementDisabled() {
		return movementDisabled;
	}

	public void setMovementDisabled(boolean movementDisabled) {
		this.movementDisabled = movementDisabled;
	}
	
	public boolean isMuted() {
		return muted;
	}
	
	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public int getMessageDelay() {
		return messageDelay;
	}

	public void setMessageDelay(int messageDelay) {
		this.messageDelay = messageDelay;
	}
	
	public int getInventoryDelay() {
		return inventoryDelay;
	}
	
	public void setInventoryDelay(int inventoryDelay) {
		this.inventoryDelay = inventoryDelay;
	}

	public MainPlayerEntity getPlayer() {
		return player;
	}

	public static final Builder builder() {
		return new Builder();
	}

	private final class TickHandler implements Runnable {
		private final Timer timer = new Timer(20, 20);
		private final Future<?> thread;

		public TickHandler() {
			thread = service.submit(this);
		}

		@Override
		public void run() {
			while(true) {
				if(thread != null && thread.isCancelled())
					return;
				timer.update();
				for(int i = 0; i < timer.getElapsedTicks(); i++)
					runTick();
				if(timer.getFPSCoolDown() > 0) {
					try {
						Thread.sleep(timer.getFPSCoolDown());
					} catch(InterruptedException exception) {}
				}
			}
		}
	}

	public static final class Builder {
		private String server;
		private int port = MinecraftBot.DEFAULT_PORT;
		private int protocol = MinecraftBot.LATEST_PROTOCOL;

		private String username;
		private String password;

		private ProxyData loginProxy;
		private ProxyData connectProxy;

		private AuthService<?> authService;
		private Session session;

		private Builder() {
		}

		public synchronized Builder server(String server) {
			this.server = server;
			return this;
		}

		public synchronized Builder port(int port) {
			this.port = port;
			return this;
		}

		public synchronized Builder protocol(int protocol) {
			this.protocol = protocol;
			return this;
		}

		public synchronized Builder username(String username) {
			this.username = username;
			return this;
		}

		public synchronized Builder password(String password) {
			this.password = password;
			return this;
		}

		public synchronized Builder loginProxy(ProxyData loginProxy) {
			this.loginProxy = loginProxy;
			return this;
		}

		public synchronized Builder connectProxy(ProxyData connectProxy) {
			this.connectProxy = connectProxy;
			return this;
		}

		public synchronized Builder authService(AuthService<?> authService) {
			this.authService = authService;
			return this;
		}

		public synchronized Builder session(Session session) {
			this.session = session;
			return this;
		}

		public synchronized MinecraftBot build() throws AuthenticationException, UnsupportedProtocolException, InvalidSessionException, IOException {
			return new MinecraftBot(this);
		}

		public String getServer() {
			return server;
		}

		public int getPort() {
			return port;
		}

		public int getProtocol() {
			return protocol;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public ProxyData getLoginProxy() {
			return loginProxy;
		}

		public ProxyData getConnectProxy() {
			return connectProxy;
		}

		public AuthService<?> getAuthService() {
			return authService;
		}

		public Session getSession() {
			return session;
		}
	}
}
