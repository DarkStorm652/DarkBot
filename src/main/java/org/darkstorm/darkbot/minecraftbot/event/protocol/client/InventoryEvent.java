package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.event.AbstractEvent;
import org.darkstorm.darkbot.minecraftbot.world.item.Inventory;

public abstract class InventoryEvent extends AbstractEvent {
	private final Inventory inventory;

	public InventoryEvent(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return inventory;
	}
}
