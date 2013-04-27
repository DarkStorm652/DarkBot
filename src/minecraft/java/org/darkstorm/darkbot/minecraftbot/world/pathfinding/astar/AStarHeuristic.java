package org.darkstorm.darkbot.minecraftbot.world.pathfinding.astar;

import java.util.List;

import org.darkstorm.darkbot.minecraftbot.world.WorldLocation;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.*;

public interface AStarHeuristic extends Heuristic {
	public WorldLocation[] getSurrounding(AStarPathSearch search,
			WorldLocation location);

	public double calculateGScore(AStarPathSearch search, PathNode node,
			boolean reverse);

	public double calculateFScore(AStarPathSearch search, PathNode node,
			boolean reverse);

	public void prioritize(List<PathNode> openSet);
}
