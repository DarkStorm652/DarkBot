package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class RequestDisconnectEvent extends ProtocolEvent {
	private final String reason;

	public RequestDisconnectEvent(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
