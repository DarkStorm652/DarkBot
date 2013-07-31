package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;

public class ChunkLoadEvent extends ProtocolEvent {
	private final int x, y, z;
	private final byte[] blocks, metadata, light, skylight, biomes;

	public ChunkLoadEvent(int x, int y, int z, byte[] blocks, byte[] metadata, byte[] light, byte[] skylight, byte[] biomes) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.blocks = blocks;
		this.metadata = metadata;
		this.light = light;
		this.skylight = skylight;
		this.biomes = biomes;
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

	public byte[] getBlocks() {
		return blocks;
	}

	public byte[] getMetadata() {
		return metadata;
	}

	public byte[] getLight() {
		return light;
	}

	public byte[] getSkylight() {
		return skylight;
	}

	public byte[] getBiomes() {
		return biomes;
	}
}
