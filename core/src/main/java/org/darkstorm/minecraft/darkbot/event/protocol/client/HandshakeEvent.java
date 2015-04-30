package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.auth.Session;
import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class HandshakeEvent extends ProtocolEvent {
	private final Session session;
	private final String server;
	private final int port;

	public HandshakeEvent(Session session, String server, int port) {
		this.session = session;
		this.server = server;
		this.port = port;
	}

	public Session getSession() {
		return session;
	}

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}
}
