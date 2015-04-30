package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public interface PathSearch {
	public void step();
	public boolean isDone();

	public BlockLocation getStart();
	public BlockLocation getEnd();

	public PathNode getPath();
	public PathSearchProvider getSource();
}
