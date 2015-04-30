package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class ChangeHeldItemEvent extends ProtocolEvent {
	private final int slot;

	public ChangeHeldItemEvent(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}
}
