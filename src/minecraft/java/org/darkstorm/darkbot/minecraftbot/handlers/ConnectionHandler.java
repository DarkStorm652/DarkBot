package org.darkstorm.darkbot.minecraftbot.handlers;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;

import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet252SharedKey;
import org.darkstorm.darkbot.minecraftbot.util.*;

public class ConnectionHandler extends MinecraftHandler implements
		EventListener {
	private static final IntHashMap<Class<? extends ReadablePacket>> defaultReadablePackets;
	private final IntHashMap<Class<? extends ReadablePacket>> readablePackets;
	public final Queue<ReadablePacket> packetProcessQueue;
	public final Queue<WriteablePacket> packetWriteQueue;
	public final Connection connection;
	private final String username, password;
	private final Proxy loginProxy;

	private Future<?> readTask;
	private Future<?> writeTask;

	private SecretKey sharedKey;

	static {
		defaultReadablePackets = new IntHashMap<Class<? extends ReadablePacket>>();
		defineDefaultPackets();
	}

	public ConnectionHandler(MinecraftBot bot, MinecraftBotData botData) {
		super(bot);
		packetProcessQueue = new ArrayDeque<ReadablePacket>();
		packetWriteQueue = new ArrayDeque<WriteablePacket>();
		readablePackets = new IntHashMap<Class<? extends ReadablePacket>>(
				defaultReadablePackets.size());
		for(int i = 0; i < 256; i++) {
			Class<? extends ReadablePacket> c = defaultReadablePackets.get(i);
			if(c != null)
				readablePackets.put(i, c);
		}
		username = botData.getUsername();
		password = botData.getPassword();
		if(botData.getHttpProxy() != null)
			loginProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					botData.getHttpProxy().getHostName(), botData
							.getHttpProxy().getPort()));
		else
			loginProxy = null;
		if(botData.getSocksProxy() != null)
			connection = new Connection(botData.getServer(), botData.getPort(),
					botData.getSocksProxy());
		else
			connection = new Connection(botData.getServer(), botData.getPort());
		bot.getEventManager().registerListener(this);
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		if(isConnected()) {
			readTask.cancel(true);
			writeTask.cancel(true);
			readTask = null;
			writeTask = null;
			connection.disconnect();
		}
	}

	private static void defineDefaultPackets() {
		try {
			String packageName = Packet.class.getPackage().getName()
					+ ".bidirectional";
			for(Class<?> c : Util.getClassesInPackage(packageName))
				if(ReadablePacket.class.isAssignableFrom(c))
					defineDefaultReadablePacket(c
							.asSubclass(ReadablePacket.class));
			packageName = Packet.class.getPackage().getName() + ".readable";
			for(Class<?> c : Util.getClassesInPackage(packageName))
				if(ReadablePacket.class.isAssignableFrom(c))
					defineDefaultReadablePacket(c
							.asSubclass(ReadablePacket.class));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void defineDefaultReadablePacket(
			Class<? extends ReadablePacket> packetClass) {
		if(packetClass == null)
			throw new NullPointerException("Null packet");
		Constructor<? extends ReadablePacket> constructor;
		try {
			constructor = packetClass.getConstructor();
		} catch(Exception exception) {
			throw new IllegalArgumentException("No default constructor");
		}
		Packet packet;
		try {
			packet = constructor.newInstance();
		} catch(Exception exception) {
			throw new IllegalArgumentException(exception);
		}
		int id = packet.getId();
		if(getDefaultReadablePacketClass(id) != null)
			throw new IllegalArgumentException("Duplicate packet id");
		defaultReadablePackets.put(id, packetClass);
	}

	private static Class<? extends ReadablePacket> getDefaultReadablePacketClass(
			int id) {
		return defaultReadablePackets.get(id);
	}

	public synchronized void defineReadablePacket(
			Class<? extends ReadablePacket> packetClass) {
		if(packetClass == null)
			throw new NullPointerException("Null packet");
		Constructor<? extends ReadablePacket> constructor;
		try {
			constructor = packetClass.getConstructor();
		} catch(Exception exception) {
			throw new IllegalArgumentException("No default constructor");
		}
		Packet packet;
		try {
			packet = constructor.newInstance();
		} catch(Exception exception) {
			throw new IllegalArgumentException(exception);
		}
		int id = packet.getId();
		if(getReadablePacketClass(id) != null)
			throw new IllegalArgumentException("Duplicate packet id");
		readablePackets.put(id, packetClass);
	}

	private ReadablePacket newReadablePacket(int id) {
		try {
			return getReadablePacketClass(id).newInstance();
		} catch(Exception exception) {}
		return null;
	}

	private synchronized Class<? extends ReadablePacket> getReadablePacketClass(
			int id) {
		return readablePackets.get(id);
	}

	public void sendPacket(WriteablePacket packet) {
		synchronized(packetWriteQueue) {
			packetWriteQueue.offer(packet);
			packetWriteQueue.notifyAll();
		}
	}

	public Session retrieveSession() throws AuthenticationException {
		String result = login(username, password);
		if(!result.contains(":"))
			throw new AuthenticationException(result);
		String[] values = result.split(":");
		return new Session(values[2], password, values[3].replaceAll("[\n\r]",
				""));
	}

	private String login(String username, String password) {
		try {
			String parameters = "user=" + URLEncoder.encode(username, "UTF-8")
					+ "&password=" + URLEncoder.encode(password, "UTF-8")
					+ "&version=" + 12;
			String result = Util.post("https://login.minecraft.net/",
					parameters, loginProxy);
			if(result == null)
				return "Unable to connect";
			if(!result.contains(":"))
				return result.trim();
			return result;
		} catch(Exception exception) {
			exception.printStackTrace();
			return "Exception: " + exception.toString();
		}
	}

	public synchronized void connect() throws IOException {
		if(connection.isConnected())
			return;
		connection.connect();
		ExecutorService service = bot.getService();
		readTask = service.submit(new ReadTask());
		writeTask = service.submit(new WriteTask());
	}

	public synchronized boolean disconnect(String reason) {
		if(!connection.isConnected() && readTask == null && writeTask == null)
			return true;
		readTask.cancel(true);
		writeTask.cancel(true);
		readTask = null;
		writeTask = null;
		boolean success = connection.disconnect();
		bot.getEventManager().sendEvent(new DisconnectEvent(reason));
		return success;
	}

	public synchronized void update() {
		ReadablePacket[] packets;
		synchronized(packetProcessQueue) {
			if(packetProcessQueue.size() == 0)
				return;
			packets = packetProcessQueue
					.toArray(new ReadablePacket[packetProcessQueue.size()]);
			packetProcessQueue.clear();
		}
		EventManager eventManager = bot.getEventManager();
		for(ReadablePacket packet : packets) {
			eventManager.sendEvent(new PacketProcessEvent(packet));
		}
	}

	public boolean isConnected() {
		return connection.isConnected() && readTask != null
				&& !readTask.isDone() && writeTask != null
				&& !writeTask.isDone();
	}

	public String getServer() {
		return connection.getHost();
	}

	public int getPort() {
		return connection.getPort();
	}

	public void setServer(String server) {
		connection.setHost(server);
	}

	public void setPort(int port) {
		connection.setPort(port);
	}

	@Override
	public String getName() {
		return "ConnectionHandler";
	}

	private final class ReadTask implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
				boolean decrypting = false;
				while(isConnected()) {
					DataInputStream in = connection.getInputStream();
					int id = in.read();
					ReadablePacket packet = newReadablePacket(id);
					if(packet == null)
						throw new IOException("Bad packet, id " + id);
					packet.readData(in);

					if(!decrypting && packet instanceof Packet252SharedKey) {
						decrypting = true;
						connection.setInputStream(new DataInputStream(
								CryptManager.decryptInputStream(sharedKey,
										connection.getInputStream())));
					}
					synchronized(packetProcessQueue) {
						packetProcessQueue.offer(packet);
					}
					// bot.getEventManager().sendEvent(
					// new PacketReceivedEvent(packet));
				}
			} catch(Throwable exception) {
				// if(exception instanceof OutOfMemoryError)
				exception.printStackTrace();
				if(readTask != null && !readTask.isCancelled())
					disconnect("Read error: " + exception);
			}
		}
	}

	private final class WriteTask implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
				boolean encrypting = false;
				while(isConnected()) {
					WriteablePacket packet = null;
					synchronized(packetWriteQueue) {
						if(!packetWriteQueue.isEmpty())
							packet = packetWriteQueue.poll();
						else
							packetWriteQueue.wait(500);
					}
					if(packet != null) {
						DataOutputStream outputStream = connection
								.getOutputStream();
						outputStream.write(packet.getId());
						packet.writeData(outputStream);
						outputStream.flush();

						if(!encrypting && packet instanceof Packet252SharedKey) {
							sharedKey = ((Packet252SharedKey) packet).sharedKey;
							encrypting = true;
							DataOutputStream encryptedOut = new DataOutputStream(
									new BufferedOutputStream(
											CryptManager.encryptOuputStream(
													sharedKey, outputStream),
											5120));
							connection.setOutputStream(encryptedOut);
						}
						// bot.getEventManager().sendEvent(
						// new PacketSentEvent(packet));
					}
				}
			} catch(Throwable exception) {
				if(writeTask != null && !writeTask.isCancelled())
					disconnect("Write error: " + exception);
			}
		}
	}
}
