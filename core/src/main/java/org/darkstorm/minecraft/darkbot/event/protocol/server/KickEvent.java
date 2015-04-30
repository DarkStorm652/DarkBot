package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class KickEvent extends ProtocolEvent {
	private final String reason;

	public KickEvent(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
