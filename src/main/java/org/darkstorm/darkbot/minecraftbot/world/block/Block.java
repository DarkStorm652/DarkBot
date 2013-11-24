package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.*;

public class Block {
	private final World world;
	private final Chunk chunk;
	private final BlockLocation location;
	private final int id, metadata;

	public Block(World world, Chunk chunk, BlockLocation location, int id,
			int metadata) {
		this.world = world;
		this.chunk = chunk;
		this.location = location;
		this.id = id;
		this.metadata = metadata;
	}

	public World getWorld() {
		return world;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public BlockLocation getLocation() {
		return location;
	}

	public int getId() {
		return id;
	}

	public int getMetadata() {
		return metadata;
	}
}
