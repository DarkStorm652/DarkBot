package org.darkstorm.darkbot.minecraftbot.events.general;

import org.darkstorm.darkbot.minecraftbot.events.Event;

public class DisconnectEvent extends Event {
	private final String reason;

	public DisconnectEvent(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
