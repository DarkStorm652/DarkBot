package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class PlayerEquipmentUpdateEvent extends EntityEvent {
	private final int slot;
	private final ItemStack item;

	public PlayerEquipmentUpdateEvent(int playerId, int slot, ItemStack item) {
		super(playerId);

		this.slot = slot;
		this.item = item;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}
}
