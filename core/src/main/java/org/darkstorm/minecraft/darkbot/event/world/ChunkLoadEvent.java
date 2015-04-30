package org.darkstorm.minecraft.darkbot.event.world;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;
import org.darkstorm.minecraft.darkbot.world.*;
import org.darkstorm.minecraft.darkbot.world.block.Chunk;

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
