package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

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
