package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class CrouchUpdateEvent extends ProtocolEvent {
	private final boolean crouching;

	public CrouchUpdateEvent(boolean crouching) {
		this.crouching = crouching;
	}

	public boolean isCrouching() {
		return crouching;
	}
}
