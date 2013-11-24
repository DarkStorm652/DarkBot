package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public abstract class BlockEvent extends ProtocolEvent {
	private final int x, y, z;

	public BlockEvent(BlockLocation location) {
		this(location.getX(), location.getY(), location.getZ());
	}

	public BlockEvent(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
}
