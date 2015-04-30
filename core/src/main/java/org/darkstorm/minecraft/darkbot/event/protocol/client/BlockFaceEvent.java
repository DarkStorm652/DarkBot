package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

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
