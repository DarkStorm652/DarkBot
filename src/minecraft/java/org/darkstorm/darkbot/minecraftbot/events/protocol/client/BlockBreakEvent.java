package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public abstract class BlockBreakEvent extends BlockFaceEvent {
	public BlockBreakEvent(BlockLocation location, int face) {
		super(location, face);
	}

	public BlockBreakEvent(int x, int y, int z, int face) {
		super(x, y, z, face);
	}
}
