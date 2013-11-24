package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public class BlockBreakStartEvent extends BlockBreakEvent {
	public BlockBreakStartEvent(BlockLocation location, int face) {
		super(location, face);
	}

	public BlockBreakStartEvent(int x, int y, int z, int face) {
		super(x, y, z, face);
	}
}
