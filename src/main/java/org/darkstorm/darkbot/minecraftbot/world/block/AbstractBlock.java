package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.*;

public abstract class AbstractBlock implements Block {
	private final World world;
	private final Chunk chunk;
	private final BlockLocation location;
	private final int id, metadata;
	private final BlockType type;

	public AbstractBlock(World world, Chunk chunk, BlockLocation location, int id, int metadata) {
		this.world = world;
		this.chunk = chunk;
		this.location = location;
		this.id = id;
		this.metadata = metadata;
		this.type = BlockType.getById(id);
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Chunk getChunk() {
		return chunk;
	}

	@Override
	public BlockLocation getLocation() {
		return location;
	}

	@Override
	public BlockType getType() {
		return type;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getMetadata() {
		return metadata;
	}
	
	@Override
	public int hashCode() {
		return location.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null || !obj.getClass().equals(this.getClass()))
			return false;
		return world.equals(((Block) obj).getWorld()) && location.equals(((Block) obj).getLocation())
				&& id == ((Block) obj).getId() && metadata == ((Block) obj).getMetadata();
	}
	
	@Override
	public String toString() {
		return "Block[id=" + id + ",meta=" + metadata+",loc=" + location + "]";
	}
}
