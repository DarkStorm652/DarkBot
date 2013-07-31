package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.item.Inventory;

public class InventoryCloseEvent extends InventoryEvent {
	public InventoryCloseEvent(Inventory inventory) {
		super(inventory);
	}
}
