package org.darkstorm.darkbot.minecraftbot.world.pathfinding.astar;

import java.util.*;

import org.darkstorm.darkbot.minecraftbot.world.WorldLocation;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.*;

public class AStarPathSearch implements PathSearch {

	private final AStarPathSearchProvider provider;
	private final AStarHeuristic heuristic;
	private final WorldLocation start, end;

	private PathNode first, last, complete, completeReverse;

	private List<PathNode> openSet, closedSet, openSetReverse,
			closedSetReverse;
	private Map<WorldLocation, PathNode> nodeWorld;

	public AStarPathSearch(AStarPathSearchProvider provider,
			AStarHeuristic heuristic, WorldLocation start, WorldLocation end) {
		this.provider = provider;
		this.heuristic = heuristic;
		this.start = start;
		this.end = end;

		nodeWorld = new HashMap<WorldLocation, PathNode>();

		first = new BasicPathNode(this, start);
		first.setGScore(heuristic.calculateGScore(this, first, false));
		first.setFScore(heuristic.calculateFScore(this, first, false));
		openSet = new ArrayList<PathNode>();
		closedSet = new ArrayList<PathNode>();
		nodeWorld.put(start, first);
		openSet.add(first);

		last = new BasicPathNode(this, end);
		last.setGScore(heuristic.calculateGScore(this, last, true));
		last.setFScore(heuristic.calculateFScore(this, last, true));
		openSetReverse = new ArrayList<PathNode>();
		closedSetReverse = new ArrayList<PathNode>();
		nodeWorld.put(end, last);
		openSetReverse.add(last);
	}

	@Override
	public void step() {
		if(isDone())
			return;

		PathNode current = heuristic.findNext(openSet);
		openSet.remove(current);

		if(complete == null && current.getLocation().equals(end)) {
			complete = reconstructPath(current);
			return;
		}
		calculate(current, false);

		if(completeReverse != null)
			return;

		PathNode currentReverse = heuristic.findNext(openSetReverse);
		openSetReverse.remove(current);

		if(completeReverse == null
				&& currentReverse.getLocation().equals(start))
			completeReverse = reconstructPath(currentReverse);
		else if(completeReverse == null)
			calculate(currentReverse, true);
	}

	private void calculate(PathNode current, boolean reverse) {
		List<PathNode> openSet = (reverse ? openSetReverse : this.openSet);
		List<PathNode> closedSet = (reverse ? closedSetReverse : this.closedSet);

		closedSet.add(current);
		for(WorldLocation adjacentLocation : heuristic.getSurrounding(this,
				current.getLocation())) {
			PathNode adjacent;
			if(!nodeWorld.containsKey(adjacentLocation)) {
				adjacent = new BasicPathNode(this, adjacentLocation);
				adjacent.setPrevious(current);
				nodeWorld.put(adjacentLocation, adjacent);
			} else
				adjacent = nodeWorld.get(adjacentLocation);

			if(closedSet.contains(adjacent)
					|| !(reverse ? heuristic.isWalkable(current, adjacent)
							&& heuristic.isWalkable(adjacent, current)
							: heuristic.isWalkable(current, adjacent)))
				continue;
			double cost = current.getGScore()
					+ heuristic.calculateGScore(this, adjacent, reverse);

			boolean contained = openSet.contains(adjacent);
			if(!contained || cost < adjacent.getGScore()) {
				if(!contained)
					openSet.add(adjacent);
				adjacent.setPrevious(current);
				current.setNext(adjacent);
				adjacent.setGScore(cost);
				adjacent.setFScore(heuristic.calculateFScore(this, adjacent,
						reverse));
			}
		}
	}

	private PathNode reconstructPath(PathNode end) {
		PathNode current = end.getPrevious(), next = end;
		while(current != null) {
			current.setNext(next);
			next = current;
			current = next.getPrevious();
		}
		return next;
	}

	@Override
	public boolean isDone() {
		return complete != null || openSet.size() == 0
				|| (openSetReverse.size() == 0 && completeReverse == null);
	}

	@Override
	public WorldLocation getStart() {
		return start;
	}

	@Override
	public WorldLocation getEnd() {
		return end;
	}

	@Override
	public PathNode getPath() {
		return complete;
	}

	@Override
	public PathSearchProvider getSource() {
		return provider;
	}

}
