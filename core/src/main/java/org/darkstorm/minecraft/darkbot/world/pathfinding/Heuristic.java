package org.darkstorm.minecraft.darkbot.world.pathfinding;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public interface Heuristic {
	public double calculateCost(BlockLocation from, BlockLocation to);
}
