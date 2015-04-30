package org.darkstorm.minecraft.darkbot.world.block;

import org.darkstorm.minecraft.darkbot.world.*;

public interface Block {
	public BlockType getType();
	public int getId();
	public int getMetadata();
	public World getWorld();
	public Chunk getChunk();
	public BlockLocation getLocation();
	public BoundingBox[] getBoundingBoxes();
	public BoundingBox getConvexBoundingBox();
}
