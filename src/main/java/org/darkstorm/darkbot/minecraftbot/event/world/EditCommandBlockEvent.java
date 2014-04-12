package org.darkstorm.darkbot.minecraftbot.event.world;

import org.darkstorm.darkbot.minecraftbot.event.AbstractEvent;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public class EditCommandBlockEvent extends AbstractEvent {
	private final BlockLocation location;

	public EditCommandBlockEvent(BlockLocation location) {
		this.location = location;
	}

	public BlockLocation getLocation() {
		return location;
	}
}
