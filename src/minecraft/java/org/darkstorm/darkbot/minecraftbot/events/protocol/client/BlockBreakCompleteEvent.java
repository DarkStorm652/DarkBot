package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public class BlockBreakCompleteEvent extends BlockBreakEvent {
	public BlockBreakCompleteEvent(BlockLocation location, int face) {
		super(location, face);
	}

	public BlockBreakCompleteEvent(int x, int y, int z, int face) {
		super(x, y, z, face);
	}
}
