package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class ItemUseEvent extends ProtocolEvent {
	private final ItemStack item;

	public ItemUseEvent(ItemStack item) {
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}
}
