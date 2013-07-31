package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;

public class SprintUpdateEvent extends ProtocolEvent {
	private final boolean sprinting;

	public SprintUpdateEvent(boolean sprinting) {
		this.sprinting = sprinting;
	}

	public boolean isSprinting() {
		return sprinting;
	}
}
