package org.darkstorm.minecraft.darkbot.world.pathfinding;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public class EuclideanHeuristic implements Heuristic {
	@Override
	public double calculateCost(BlockLocation from, BlockLocation to) {
		return from.getDistanceTo(to);
	}
}
