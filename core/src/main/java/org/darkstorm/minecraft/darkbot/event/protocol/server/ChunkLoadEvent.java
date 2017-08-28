package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class ChunkLoadEvent extends ProtocolEvent {
	private final int x, y, z;
	private final BlockStorage blocks;
	private final NibbleArray3d light, skylight;
	private final byte[] biomes;

	public ChunkLoadEvent(int x, int y, int z, BlockStorage blocks, NibbleArray3d light, NibbleArray3d skylight, byte[] biomes) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.blocks = blocks;
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

	public BlockStorage getBlocks() {
		return blocks;
	}

	public NibbleArray3d getLight() {
		return light;
	}

	public NibbleArray3d getSkylight() {
		return skylight;
	}

	public byte[] getBiomes() {
		return biomes;
	}
}
