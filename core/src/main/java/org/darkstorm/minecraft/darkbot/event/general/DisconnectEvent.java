package org.darkstorm.minecraft.darkbot.event.general;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;

public class DisconnectEvent extends AbstractEvent {
	private final String reason;

	public DisconnectEvent(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
