package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.*;

public abstract class AbstractBlock implements Block {
	private static final BoundingBox EMPTY = BoundingBox.getBoundingBox(0, 0, 0, 0, 0, 0);
	
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
	public BoundingBox getConvexBoundingBox() {
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, maxZ = Double.MIN_VALUE;
		
		for(BoundingBox box : getBoundingBoxes()) {
			minX = Math.min(minX, box.getMinX());
			minY = Math.min(minY, box.getMinY());
			minZ = Math.min(minZ, box.getMinZ());
			maxX = Math.max(maxX, box.getMaxX());
			maxY = Math.max(maxY, box.getMaxY());
			maxZ = Math.max(maxZ, box.getMaxZ());
		}
		
		if(minX > maxX || minY > maxY || minZ > maxZ)
			return EMPTY;
		return BoundingBox.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
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
