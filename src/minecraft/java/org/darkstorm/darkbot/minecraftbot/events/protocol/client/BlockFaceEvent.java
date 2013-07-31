package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public abstract class BlockFaceEvent extends BlockEvent {
	private final int face;

	public BlockFaceEvent(BlockLocation location, int face) {
		super(location);

		this.face = face;
	}

	public BlockFaceEvent(int x, int y, int z, int face) {
		super(x, y, z);

		this.face = face;
	}

	public int getFace() {
		return face;
	}
}
