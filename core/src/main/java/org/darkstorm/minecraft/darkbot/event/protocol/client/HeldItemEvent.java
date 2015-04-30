package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public abstract class HeldItemEvent extends InventoryEvent {
	public HeldItemEvent(PlayerInventory inventory) {
		super(inventory);
	}

	@Override
	public PlayerInventory getInventory() {
		return (PlayerInventory) super.getInventory();
	}
}
