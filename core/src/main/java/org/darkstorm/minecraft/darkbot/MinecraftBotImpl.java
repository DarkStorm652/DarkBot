package org.darkstorm.minecraft.darkbot;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import org.darkstorm.minecraft.darkbot.ai.*;
import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.general.*;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.*;
import org.darkstorm.minecraft.darkbot.event.world.SpawnEvent;
import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.util.*;
import org.darkstorm.minecraft.darkbot.world.*;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.*;

public class MinecraftBotImpl implements MinecraftBot, EventListener {
	public static final int DEFAULT_PORT = 25565;
	public static final int MAX_CHAT_LENGTH = 100;
	public static final UUID CLIENT_TOKEN = UUIDUtils.generateSystemUUID("CLIENT_TOKEN");

	private final ExecutorService service;
	private final EventBus eventBus;
	private final TaskManager taskManager;
	private final Session connectionHandler;

	private final String username;

	private MainPlayerEntity player;
	private World world;

	private double lastX, lastY, lastZ;
	private float lastYaw, lastPitch;
	private boolean hasSpawned, movementDisabled, muted;
	private int messageDelay, inventoryDelay;
	private long lastMessage;
	
	private MinecraftBotImpl(Builder builder) throws IOException {
		service = Executors.newCachedThreadPool();
		eventBus = new ConcurrentEventBus();
		eventBus.register(this);
		taskManager = new BasicTaskManager(this);
		username = builder.username;

		Protocol335 protocol = new Protocol335(this);
		MinecraftProtocol mcProtocol = new MinecraftProtocol(getUsername());
		Client client = new Client(builder.getServer(), builder.getPort(), mcProtocol, new TcpSessionFactory()); //TODO: Implement proxy

		connectionHandler = client.getSession();
		connectionHandler.addListener(new SessionAdapter() {
			@Override
			public void packetReceived(PacketReceivedEvent event) {
				protocol.onPacketReceived(event);
			}

			@Override
			public void packetSent(PacketSentEvent event) {
				super.packetSent(event);
			}

			@Override
			public void disconnected(DisconnectedEvent event) {
				//TODO: Check
			}
		});

		connectionHandler.connect();
		new TickHandler();
	}


	@EventHandler
	public void onLogin(LoginEvent event) {
		setWorld(new BasicWorld(this, event.getWorldType(), event.getDimension(), event.getDifficulty(), event.getWorldHeight()));
		player = new MainPlayerEntity(world, event.getPlayerId(), getUsername(), event.getGameMode());
		world.spawnEntity(player);
	}

	@EventHandler
	public void onRespawn(RespawnEvent event) {
		if(world.getDimension() != event.getRespawnDimension())
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
		player.setWindow(new GenericInventory(this, event.getWindowId(), event.getSlotCount()));
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
			if(hasSpawned && !player.getInventory().hasActionsQueued() && (player.getWindow() == null || !player.getWindow().hasActionsQueued())) {
				taskManager.update();
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
		double x = player.getX(), y = player.getY(), z = player.getZ();
		float yaw = player.getYaw(), pitch = player.getPitch();
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

	@Override
	public synchronized void sendChat(String message) {
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

	@Override
	public boolean hasSpawned() {
		return hasSpawned;
	}

	@Override
	public synchronized World getWorld() {
		return world;
	}

	public synchronized void setWorld(World world) {
		if(this.world != null)
			this.world.destroy();
		this.world = world;
		if(player != null) {
			if(world != null) {
                player = new MainPlayerEntity(world, player.getId(), player.getName(), player.getGameMode());
                world.spawnEntity(player);
            } else
				player = null;
		}
	}

	public ExecutorService getService() {
		return service;
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public TaskManager getTaskManager() {
		return taskManager;
	}

	@Override
	public Session getConnectionHandler() {
		return connectionHandler;
	}

	@Override
	public boolean isConnected() {
		return connectionHandler.isConnected();
	}

	@Override
	public void disconnect(String reason) {
		connectionHandler.disconnect(reason);
	}

	@Override
	public boolean isMovementDisabled() {
		return movementDisabled;
	}

	@Override
	public void setMovementDisabled(boolean movementDisabled) {
		this.movementDisabled = movementDisabled;
	}

	@Override
	public boolean isMuted() {
		return muted;
	}

	@Override
	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public int getMessageDelay() {
		return messageDelay;
	}

	public void setMessageDelay(int messageDelay) {
		this.messageDelay = messageDelay;
	}

	@Override
	public int getInventoryDelay() {
		return inventoryDelay;
	}

	@Override
	public void setInventoryDelay(int inventoryDelay) {
		this.inventoryDelay = inventoryDelay;
	}

	@Override
	public MainPlayerEntity getPlayer() {
		return player;
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getUsername() {
		return username;
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
		private int port = MinecraftBotImpl.DEFAULT_PORT;

		private String username;

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

		public synchronized Builder username(String username) {
			this.username = username;
			return this;
		}

		public synchronized MinecraftBotImpl build() throws IOException {
			return new MinecraftBotImpl(this);
		}

		public String getServer() {
			return server;
		}

		public int getPort() {
			return port;
		}
	}
}
