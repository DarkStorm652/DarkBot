package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public class BlockBreakStopEvent extends BlockBreakEvent {
	public BlockBreakStopEvent(BlockLocation location, int face) {
		super(location, face);
	}

	public BlockBreakStopEvent(int x, int y, int z, int face) {
		super(x, y, z, face);
	}
}
