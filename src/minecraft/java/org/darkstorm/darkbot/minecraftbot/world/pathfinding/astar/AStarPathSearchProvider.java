package org.darkstorm.darkbot.minecraftbot.world.pathfinding.astar;

import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.PathSearchProvider;

public class AStarPathSearchProvider implements PathSearchProvider {
	private final World world;

	private AStarHeuristic heuristic;

	public AStarPathSearchProvider(World world) {
		this.world = world;

		heuristic = new BasicAStarHeuristic(this);
	}

	@Override
	public AStarPathSearch provideSearch(BlockLocation start, BlockLocation end) {
		return provideSearch(new WorldLocation(start), new WorldLocation(end));
	}

	@Override
	public AStarPathSearch provideSearch(WorldLocation start, WorldLocation end) {
		return new AStarPathSearch(this, heuristic, start, end);
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public AStarHeuristic getHeuristic() {
		return heuristic;
	}
}
