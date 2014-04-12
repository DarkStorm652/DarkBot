package org.darkstorm.darkbot.minecraftbot.event.world;

import org.darkstorm.darkbot.minecraftbot.event.AbstractEvent;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public class EditSignEvent extends AbstractEvent {
	private final BlockLocation location;

	public EditSignEvent(BlockLocation location) {
		this.location = location;
	}

	public BlockLocation getLocation() {
		return location;
	}
}
