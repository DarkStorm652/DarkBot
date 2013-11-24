package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

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
