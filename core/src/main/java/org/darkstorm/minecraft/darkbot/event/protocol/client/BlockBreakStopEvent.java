package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public class BlockBreakStopEvent extends BlockBreakEvent {
	public BlockBreakStopEvent(BlockLocation location, int face) {
		super(location, face);
	}

	public BlockBreakStopEvent(int x, int y, int z, int face) {
		super(x, y, z, face);
	}
}
