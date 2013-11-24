package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public interface Heuristic {
	public boolean isWalkable(PathNode current, PathNode node);

	public boolean isClimbableBlock(BlockLocation location);

	public PathSearchProvider getProvider();
}
