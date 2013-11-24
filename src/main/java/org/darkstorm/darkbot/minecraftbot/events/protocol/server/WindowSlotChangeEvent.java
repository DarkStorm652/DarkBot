package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class WindowSlotChangeEvent extends WindowEvent {
	private final int slot;
	private final ItemStack newItem;

	public WindowSlotChangeEvent(int windowId, int slot, ItemStack newItem) {
		super(windowId);

		this.slot = slot;
		this.newItem = newItem;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getNewItem() {
		return newItem;
	}
}
