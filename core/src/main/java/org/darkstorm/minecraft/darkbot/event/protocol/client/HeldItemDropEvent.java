package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.item.PlayerInventory;

public class HeldItemDropEvent extends HeldItemEvent {
	private final boolean entireStack;

	public HeldItemDropEvent(PlayerInventory inventory, boolean entireStack) {
		super(inventory);

		this.entireStack = entireStack;
	}

	public boolean isEntireStack() {
		return entireStack;
	}
}
