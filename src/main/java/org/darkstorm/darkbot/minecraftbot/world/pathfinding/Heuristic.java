package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public interface Heuristic {
	public double calculateCost(BlockLocation from, BlockLocation to);
}
