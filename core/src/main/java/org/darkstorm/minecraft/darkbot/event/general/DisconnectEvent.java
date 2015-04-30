package org.darkstorm.darkbot.minecraftbot.event.general;

import org.darkstorm.darkbot.minecraftbot.event.AbstractEvent;

public class DisconnectEvent extends AbstractEvent {
	private final String reason;

	public DisconnectEvent(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
