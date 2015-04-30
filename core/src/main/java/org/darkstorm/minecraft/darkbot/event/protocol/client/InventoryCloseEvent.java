package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.item.Inventory;

public class InventoryCloseEvent extends InventoryEvent {
	public InventoryCloseEvent(Inventory inventory) {
		super(inventory);
	}
}
