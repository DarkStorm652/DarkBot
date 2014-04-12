package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public class HeldItemChangeEvent extends HeldItemEvent {
	private final int oldSlot, newSlot;

	public HeldItemChangeEvent(PlayerInventory inventory, int oldSlot, int newSlot) {
		super(inventory);

		this.oldSlot = oldSlot;
		this.newSlot = newSlot;
	}

	public int getOldSlot() {
		return oldSlot;
	}

	public int getNewSlot() {
		return newSlot;
	}
}
