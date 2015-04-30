package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.event.AbstractCancellableEvent;
import org.darkstorm.minecraft.darkbot.world.item.Inventory;

public abstract class InventoryEvent extends AbstractCancellableEvent {
	private final Inventory inventory;

	public InventoryEvent(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return inventory;
	}
}
