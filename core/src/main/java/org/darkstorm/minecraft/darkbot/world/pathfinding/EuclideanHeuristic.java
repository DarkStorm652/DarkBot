package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public class EuclideanHeuristic implements Heuristic {
	@Override
	public double calculateCost(BlockLocation from, BlockLocation to) {
		return from.getDistanceTo(to);
	}
}
