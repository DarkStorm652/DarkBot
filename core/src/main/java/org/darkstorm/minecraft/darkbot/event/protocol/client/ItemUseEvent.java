package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

public class ItemUseEvent extends ProtocolEvent {
	private final ItemStack item;

	public ItemUseEvent(ItemStack item) {
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}
}
