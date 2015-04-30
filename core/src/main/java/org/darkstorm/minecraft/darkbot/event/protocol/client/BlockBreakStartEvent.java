package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public class BlockBreakStartEvent extends BlockBreakEvent {
	public BlockBreakStartEvent(BlockLocation location, int face) {
		super(location, face);
	}

	public BlockBreakStartEvent(int x, int y, int z, int face) {
		super(x, y, z, face);
	}
}
