package org.darkstorm.minecraft.darkbot.world.pathfinding;

import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public interface PathSearchProvider {
	public PathSearch provideSearch(BlockLocation start, BlockLocation end);
	// public PathSearch provideSearch(WorldLocation start, WorldLocation end);

	public Heuristic getHeuristic();
	public WorldPhysics getWorldPhysics();

	public World getWorld();
}
