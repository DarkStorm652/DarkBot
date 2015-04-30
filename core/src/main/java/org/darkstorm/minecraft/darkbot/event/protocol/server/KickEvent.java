package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class KickEvent extends ProtocolEvent {
	private final String reason;

	public KickEvent(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
