package org.darkstorm.darkbot.minecraftbot;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.PublicKey;
import java.util.Random;
import java.util.concurrent.*;

import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.bot.*;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.general.*;
import org.darkstorm.darkbot.minecraftbot.events.io.*;
import org.darkstorm.darkbot.minecraftbot.events.world.SpawnEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.*;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.util.Timer;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

@BotManifest(name = "MinecraftBot", botDataClass = MinecraftBotData.Builder.class)
public class MinecraftBot extends Bot implements EventListener {
	public static final int DEFAULT_PORT = 25565;
	public static final int PROTOCOL_VERSION = 61;

	private final ExecutorService service;
	private final EventManager eventManager;
	private final TaskManager taskManager;
	private final ConnectionHandler connectionHandler;
	private final Session session;
	private final Proxy loginProxy;

	private MainPlayerEntity player;
	private World world;

	private boolean hasSpawned = false, movementDisabled = false;
	private Random random = new Random();
	private int keepAliveTimer = 0, messageDelay = 2000;
	private long lastMessage;
	private Activity activity;

	public MinecraftBot(DarkBot darkBot, MinecraftBotData.Builder botData) {
		this(darkBot, botData.build());
	}

	public MinecraftBot(DarkBot darkBot, MinecraftBotData botData) {
		super(darkBot);
		service = Executors.newCachedThreadPool();
		eventManager = new EventManager();
		eventManager.registerListener(this);
		taskManager = new BasicTaskManager(this);
		connectionHandler = new ConnectionHandler(this, botData);
		if(botData.getHttpProxy() != null)
			loginProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(botData.getHttpProxy().getHostName(), botData.getHttpProxy().getPort()));
		else
			loginProxy = null;
		if(botData.getPassword() != null && !botData.getPassword().isEmpty()) {
			try {
				session = connectionHandler.retrieveSession();
			} catch(AuthenticationException exception) {
				throw new RuntimeException("Invalid login info", exception);
			}
		} else
			session = new Session(botData.getUsername(), botData.getPassword(), botData.getSessionId());
		try {
			connectionHandler.connect();
		} catch(IOException exception) {
			throw new RuntimeException("Unable to connect!", exception);
		}
		connectionHandler.sendPacket(new Packet2Handshake(PROTOCOL_VERSION, session.getUsername(), connectionHandler.getServer(), connectionHandler.getPort()));
		new TickHandler();
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet1Login) {
			Packet1Login loginPacket = (Packet1Login) packet;
			setWorld(new BasicWorld(this, loginPacket.worldType, loginPacket.dimension, loginPacket.difficulty, loginPacket.worldHeight));
			player = new MainPlayerEntity(world, loginPacket.playerId, session.getUsername(), loginPacket.gameMode);
			connectionHandler.sendPacket(new Packet204ClientInfo("en_US", 1, 0, true, 2, true));
		} else if(packet instanceof Packet9Respawn) {
			Packet9Respawn respawnPacket = (Packet9Respawn) packet;
			setWorld(new BasicWorld(this, respawnPacket.worldType, respawnPacket.respawnDimension, respawnPacket.difficulty, respawnPacket.worldHeight));
			player.setGameMode(respawnPacket.gameMode);
		} else if(packet instanceof Packet13PlayerLookMove) {
			Packet13PlayerLookMove lookMovePacket = (Packet13PlayerLookMove) packet;
			player.setX(lookMovePacket.x);
			player.setY(lookMovePacket.y);
			player.setZ(lookMovePacket.z);
			player.setYaw(lookMovePacket.yaw);
			player.setPitch(lookMovePacket.pitch);
			connectionHandler.sendPacket(lookMovePacket);
			if(!hasSpawned)
				eventManager.sendEvent(new SpawnEvent(player));
			hasSpawned = true;
		} else if(packet instanceof Packet100OpenWindow) {
			if(player == null)
				return;
			Packet100OpenWindow openWindowPacket = (Packet100OpenWindow) packet;
			System.out.println("Opened inventory " + openWindowPacket.inventoryType + ": " + openWindowPacket.slotsCount + " slots");
			switch(openWindowPacket.inventoryType) {
			case 0:
				player.setWindow(new ChestInventory(this, openWindowPacket.windowId, openWindowPacket.slotsCount == 27 ? false : true));
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			}
		} else if(packet instanceof Packet101CloseWindow) {
			if(player == null)
				return;
			player.setWindow(null);
		} else if(packet instanceof Packet103SetSlot) {
			if(player == null)
				return;
			Inventory window = player.getWindow();
			Packet103SetSlot slotPacket = (Packet103SetSlot) packet;
			if(slotPacket.windowId != 0 && (window == null || slotPacket.windowId != window.getWindowId()))
				return;
			if(slotPacket.windowId == 0)
				player.getInventory().setItemFromServerAt(slotPacket.itemSlot, slotPacket.itemStack);
			else
				window.setItemFromServerAt(slotPacket.itemSlot, slotPacket.itemStack);
		} else if(packet instanceof Packet104WindowItems) {
			if(player == null)
				return;
			Inventory window = player.getWindow();
			Packet104WindowItems itemsPacket = (Packet104WindowItems) packet;
			if(itemsPacket.windowId != 0 && (window == null || itemsPacket.windowId != window.getWindowId()))
				return;
			ItemStack[] items = itemsPacket.itemStack;
			if(itemsPacket.windowId == 0)
				for(int i = 0; i < items.length; i++)
					player.getInventory().setItemFromServerAt(i, items[i]);
			else
				for(int i = 0; i < items.length; i++)
					window.setItemFromServerAt(i, items[i]);
		} else if(packet instanceof Packet255KickDisconnect) {
			connectionHandler.disconnect("Kicked: " + ((Packet255KickDisconnect) packet).reason);
		} else if(packet instanceof Packet252SharedKey) {
			connectionHandler.sendPacket(new Packet205ClientCommand(0));
		} else if(packet instanceof Packet253EncryptionKeyRequest) {
			handleServerAuthData((Packet253EncryptionKeyRequest) packet);
		}
	}

	public void handleServerAuthData(Packet253EncryptionKeyRequest keyRequest) {
		String serverId = keyRequest.serverId.trim();
		PublicKey publicKey = keyRequest.publicKey;
		SecretKey secretKey = CryptManager.generateSecretKey();

		if(!serverId.equals("-")) {
			String hash = new BigInteger(CryptManager.encrypt(serverId, publicKey, secretKey)).toString(16);
			if(session.getSessionId() != null) {
				String response = authenticate(session.getUsername(), session.getSessionId(), hash);

				if(response == null)
					return;

				if(!response.equalsIgnoreCase("ok")) {
					connectionHandler.disconnect("Failed login: " + response);
					return;
				}
			}
		}

		connectionHandler.sendPacket(new Packet252SharedKey(secretKey, publicKey, keyRequest.verifyToken));
	}

	private String authenticate(String username, String sessionId, String serverId) {
		try {
			URL url = new URL((new StringBuilder()).append("http://session.minecraft.net/game/joinserver.jsp?user=").append(encodeUtf8(username)).append("&sessionId=").append(encodeUtf8(sessionId)).append("&serverId=").append(encodeUtf8(serverId)).toString());
			BufferedReader bufferedreader;
			if(loginProxy != null) {
				URLConnection connection = url.openConnection(loginProxy);
				connection.setConnectTimeout(30000);
				connection.setReadTimeout(30000);
				bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(30000);
				connection.setReadTimeout(30000);
				bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			}
			String response = bufferedreader.readLine();
			bufferedreader.close();

			return response;
		} catch(Exception exception) {
			exception.printStackTrace();
			connectionHandler.disconnect("Internal error handling handshake: " + exception);
			return null;
		}
	}

	private static String encodeUtf8(String par0Str) throws IOException {
		return URLEncoder.encode(par0Str, "UTF-8");
	}

	public synchronized void runTick() {
		connectionHandler.update();

		if(hasSpawned) {
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

			updateKeepAlive();
			if(!movementDisabled)
				updateMovement();
		}

		eventManager.sendEvent(new TickEvent());
	}

	private synchronized void updateKeepAlive() {
		if(keepAliveTimer <= 0) {
			connectionHandler.sendPacket(new Packet0KeepAlive(random.nextInt()));
			keepAliveTimer = 30;
		} else
			keepAliveTimer--;
	}

	public synchronized void updateMovement() {
		double x = player.getX(), y = player.getY(), z = player.getZ(), yaw = player.getYaw(), pitch = player.getPitch();
		boolean move = x != player.getLastX() || y != player.getLastY() || z != player.getLastZ();
		boolean rotate = yaw != player.getLastYaw() || pitch != player.getLastPitch();
		boolean onGround = player.isOnGround();
		Packet10Flying packet;
		if(move && rotate) {
			player.setLastX(x);
			player.setLastY(y);
			player.setLastZ(z);
			player.setLastYaw(yaw);
			player.setLastPitch(pitch);
			packet = new Packet13PlayerLookMove(x, y, y + 1.62000000476837, z, (float) yaw, (float) pitch, onGround);
		} else if(move) {
			player.setLastX(x);
			player.setLastY(y);
			player.setLastZ(z);
			packet = new Packet11PlayerPosition(x, y, y + 1.62000000476837, z, onGround);
		} else if(rotate) {
			player.setLastYaw(yaw);
			player.setLastPitch(pitch);
			packet = new Packet12PlayerLook((float) yaw, (float) pitch, onGround);
		} else
			packet = new Packet10Flying(onGround);
		connectionHandler.sendPacket(packet);
	}

	@EventHandler
	public void onPacketSent(PacketSentEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet101CloseWindow) {
			if(player == null)
				return;
			if(((Packet101CloseWindow) packet).windowId != 0)
				player.setWindow(null);
		}
	}

	@EventHandler
	public synchronized void onDisconnect(DisconnectEvent event) {
		service.shutdownNow();
		eventManager.clearListeners();
		hasSpawned = false;
		player = null;
		world = null;
	}

	public synchronized void say(String message) {
		while(message.length() > Packet3Chat.MAX_CHAT_LENGTH) {
			long elapsed = System.currentTimeMillis() - lastMessage;
			if(elapsed < messageDelay) {
				try {
					Thread.sleep(messageDelay - elapsed);
				} catch(InterruptedException e) {}
			}
			String part = message.substring(0, Packet3Chat.MAX_CHAT_LENGTH);
			connectionHandler.sendPacket(new Packet3Chat(part));
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
			connectionHandler.sendPacket(new Packet3Chat(message));
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
		if(player != null)
			if(world != null)
				player = new MainPlayerEntity(world, player.getId(), player.getName(), player.getGameMode());
			else
				player = null;
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

	public EventManager getEventManager() {
		return eventManager;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	@Override
	public boolean isConnected() {
		return connectionHandler.isConnected();
	}

	public boolean isMovementDisabled() {
		return movementDisabled;
	}

	public void setMovementDisabled(boolean movementDisabled) {
		this.movementDisabled = movementDisabled;
	}

	public int getMessageDelay() {
		return messageDelay;
	}

	public void setMessageDelay(int messageDelay) {
		this.messageDelay = messageDelay;
	}

	public MainPlayerEntity getPlayer() {
		return player;
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
}
