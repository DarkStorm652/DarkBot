package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class BlockChangeEvent extends ProtocolEvent {
	private final int id, metadata, x, y, z;

	public BlockChangeEvent(int id, int metadata, int x, int y, int z) {
		this.id = id;
		this.metadata = metadata;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getId() {
		return id;
	}

	public int getMetadata() {
		return metadata;
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
