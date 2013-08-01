package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;
import java.security.Security;
import java.util.*;
import java.util.concurrent.*;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.*;
import org.darkstorm.darkbot.minecraftbot.util.Connection;

public class SocketConnectionHandler implements ConnectionHandler, EventListener {
	private final MinecraftBot bot;
	private final Protocol protocol;
	private final Queue<ReadablePacket> packetProcessQueue;
	private final Queue<WriteablePacket> packetWriteQueue;
	private final Connection connection;

	private ReadTask readTask;
	private WriteTask writeTask;

	private SecretKey sharedKey;
	private boolean encrypting, decrypting;

	public SocketConnectionHandler(MinecraftBot bot, MinecraftBotData botData, Protocol protocol) {
		this.bot = bot;
		this.protocol = protocol;
		packetProcessQueue = new ArrayDeque<ReadablePacket>();
		packetWriteQueue = new ArrayDeque<WriteablePacket>();
		if(botData.getSocksProxy() != null)
			connection = new Connection(botData.getServer(), botData.getPort(), botData.getSocksProxy());
		else
			connection = new Connection(botData.getServer(), botData.getPort());
		bot.getEventManager().registerListener(this);
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		if(isConnected())
			connection.disconnect();
	}

	@Override
	public void sendPacket(WriteablePacket packet) {
		synchronized(packetWriteQueue) {
			packetWriteQueue.offer(packet);
			packetWriteQueue.notifyAll();
		}
	}

	@Override
	public synchronized void connect() throws IOException {
		if(connection.isConnected())
			return;
		connection.connect();

		ExecutorService service = bot.getService();
		readTask = new ReadTask();
		writeTask = new WriteTask();
		readTask.future = service.submit(readTask);
		writeTask.future = service.submit(writeTask);
	}

	@Override
	public synchronized void disconnect(String reason) {
		if(!connection.isConnected() && readTask.future == null && writeTask.future == null)
			return;
		if(readTask != null)
			readTask.future.cancel(true);
		if(writeTask != null)
			writeTask.future.cancel(true);
		readTask = null;
		writeTask = null;
		sharedKey = null;
		encrypting = decrypting = false;
		connection.disconnect();
		bot.getEventManager().sendEvent(new DisconnectEvent(reason));
	}

	@Override
	public synchronized void process() {
		ReadablePacket[] packets;
		synchronized(packetProcessQueue) {
			if(packetProcessQueue.size() == 0)
				return;
			packets = packetProcessQueue.toArray(new ReadablePacket[packetProcessQueue.size()]);
			packetProcessQueue.clear();
		}
		EventManager eventManager = bot.getEventManager();
		for(ReadablePacket packet : packets)
			eventManager.sendEvent(new PacketProcessEvent(packet));
	}

	@Override
	public Protocol getProtocol() {
		return protocol;
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected() && readTask != null && !readTask.future.isDone() && writeTask != null && !writeTask.future.isDone();
	}

	@Override
	public String getServer() {
		return connection.getHost();
	}

	@Override
	public int getPort() {
		return connection.getPort();
	}

	@Override
	public boolean supportsEncryption() {
		return true;
	}

	@Override
	public SecretKey getSharedKey() {
		return sharedKey;
	}

	@Override
	public void setSharedKey(SecretKey sharedKey) {
		if(this.sharedKey != null)
			throw new IllegalStateException("Shared key already set");
		this.sharedKey = sharedKey;
	}

	@Override
	public boolean isEncrypting() {
		return encrypting;
	}

	@Override
	public synchronized void enableEncryption() throws UnsupportedOperationException {
		if(!isConnected())
			throw new IllegalStateException("Not connected");
		if(encrypting)
			throw new IllegalStateException("Already encrypting");
		if(sharedKey == null)
			throw new IllegalStateException("Shared key not set");
		if(writeTask.thread == null || writeTask.thread != Thread.currentThread())
			throw new IllegalStateException("Must be called from write thread");
		connection.setOutputStream(new DataOutputStream(EncryptionUtil.encryptOutputStream(connection.getOutputStream(), sharedKey)));
		encrypting = true;
	}

	@Override
	public boolean isDecrypting() {
		return decrypting;
	}

	@Override
	public synchronized void enableDecryption() throws UnsupportedOperationException {
		if(!isConnected())
			throw new IllegalStateException("Not connected");
		if(decrypting)
			throw new IllegalStateException("Already decrypting");
		if(sharedKey == null)
			throw new IllegalStateException("Shared key not set");
		if(readTask.thread == null || readTask.thread != Thread.currentThread())
			throw new IllegalStateException("Must be called from read thread");
		connection.setInputStream(new DataInputStream(EncryptionUtil.decryptInputStream(connection.getInputStream(), sharedKey)));
		decrypting = true;
	}

	private final class ReadTask implements Runnable {
		private Future<?> future;
		private Thread thread;

		@Override
		public void run() {
			thread = Thread.currentThread();
			try {
				Thread.sleep(500);
				while(isConnected()) {
					DataInputStream in = connection.getInputStream();
					int id = in.read();
					ReadablePacket packet = (ReadablePacket) protocol.createPacket(id);
					if(packet == null || !(packet instanceof ReadablePacket))
						throw new IOException("Bad packet, id " + id);
					packet.readData(in);

					bot.getEventManager().sendEvent(new PacketReceivedEvent(packet));
					synchronized(packetProcessQueue) {
						packetProcessQueue.offer(packet);
					}
				}
			} catch(Throwable exception) {
				exception.printStackTrace();
				disconnect("Read error: " + exception);
			}
		}
	}

	private final class WriteTask implements Runnable {
		private Future<?> future;
		private Thread thread;

		@Override
		public void run() {
			thread = Thread.currentThread();
			try {
				Thread.sleep(500);
				while(isConnected()) {
					WriteablePacket packet = null;
					try {
						synchronized(packetWriteQueue) {
							if(!packetWriteQueue.isEmpty())
								packet = packetWriteQueue.poll();
							else
								packetWriteQueue.wait(500);
						}
					} catch(InterruptedException exception) {
						if(future == null || future.isCancelled())
							break;
						continue;
					}
					if(packet != null) {
						DataOutputStream outputStream = connection.getOutputStream();
						outputStream.write(packet.getId());
						packet.writeData(outputStream);
						outputStream.flush();

						bot.getEventManager().sendEvent(new PacketSentEvent(packet));
					}
				}
			} catch(Throwable exception) {
				exception.printStackTrace();
				disconnect("Write error: " + exception);
			}
		}
	}

	static {
		Security.addProvider(new BouncyCastleProvider());
	}
}
