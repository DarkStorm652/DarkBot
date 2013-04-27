package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public interface PathSearchProvider {
	public PathSearch provideSearch(BlockLocation start, BlockLocation end);

	public PathSearch provideSearch(WorldLocation start, WorldLocation end);

	public World getWorld();

	public Heuristic getHeuristic();
}
