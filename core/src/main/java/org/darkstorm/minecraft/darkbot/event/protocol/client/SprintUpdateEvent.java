package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class SprintUpdateEvent extends ProtocolEvent {
	private final boolean sprinting;

	public SprintUpdateEvent(boolean sprinting) {
		this.sprinting = sprinting;
	}

	public boolean isSprinting() {
		return sprinting;
	}
}
