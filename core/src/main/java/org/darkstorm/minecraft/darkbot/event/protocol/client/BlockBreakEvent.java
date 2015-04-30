package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public abstract class BlockBreakEvent extends BlockFaceEvent {
	public BlockBreakEvent(BlockLocation location, int face) {
		super(location, face);
	}

	public BlockBreakEvent(int x, int y, int z, int face) {
		super(x, y, z, face);
	}
}
