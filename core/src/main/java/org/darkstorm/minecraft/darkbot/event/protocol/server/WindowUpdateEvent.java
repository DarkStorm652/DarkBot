package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

public class WindowUpdateEvent extends WindowEvent {
	private final ItemStack[] items;

	public WindowUpdateEvent(int windowId, ItemStack[] items) {
		super(windowId);

		this.items = items;
	}

	public ItemStack[] getItems() {
		return items.clone();
	}
}
