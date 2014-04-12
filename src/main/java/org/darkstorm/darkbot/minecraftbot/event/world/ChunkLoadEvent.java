package org.darkstorm.darkbot.minecraftbot.event.world;

import org.darkstorm.darkbot.minecraftbot.event.AbstractEvent;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.Chunk;

public class ChunkLoadEvent extends AbstractEvent {
	private final World world;
	private final Chunk chunk;

	public ChunkLoadEvent(World world, Chunk chunk) {
		this.world = world;
		this.chunk = chunk;
	}

	public World getWorld() {
		return world;
	}

	public Chunk getChunk() {
		return chunk;
	}
}
