package org.darkstorm.minecraft.darkbot.world.pathfinding;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public interface PathSearch {
	public void step();
	public boolean isDone();

	public BlockLocation getStart();
	public BlockLocation getEnd();

	public PathNode getPath();
	public PathSearchProvider getSource();
}
