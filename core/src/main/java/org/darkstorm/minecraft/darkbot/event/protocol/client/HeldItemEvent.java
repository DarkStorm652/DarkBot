package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.item.PlayerInventory;

public abstract class HeldItemEvent extends InventoryEvent {
	public HeldItemEvent(PlayerInventory inventory) {
		super(inventory);
	}

	@Override
	public PlayerInventory getInventory() {
		return (PlayerInventory) super.getInventory();
	}
}
