package org.darkstorm.darkbot.minecraftbot.events.world;

import org.darkstorm.darkbot.minecraftbot.events.Event;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public class EditSignEvent extends Event {
	private final BlockLocation location;

	public EditSignEvent(BlockLocation location) {
		this.location = location;
	}

	public BlockLocation getLocation() {
		return location;
	}
}
