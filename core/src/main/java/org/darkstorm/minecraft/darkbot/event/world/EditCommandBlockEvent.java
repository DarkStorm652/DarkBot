package org.darkstorm.minecraft.darkbot.event.world;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;
import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public class EditCommandBlockEvent extends AbstractEvent {
	private final BlockLocation location;

	public EditCommandBlockEvent(BlockLocation location) {
		this.location = location;
	}

	public BlockLocation getLocation() {
		return location;
	}
}
