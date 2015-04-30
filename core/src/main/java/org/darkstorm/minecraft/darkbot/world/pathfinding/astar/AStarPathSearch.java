package org.darkstorm.minecraft.darkbot.world.pathfinding.astar;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;
import org.darkstorm.minecraft.darkbot.world.pathfinding.*;

public class AStarPathSearch implements PathSearch {
	private static final PathNodeComparator PATH_NODE_COMPARATOR = new PathNodeComparator();

	private final AStarPathSearchProvider provider;
	private final Heuristic heuristic;
	private final WorldPhysics physics;
	private final BlockLocation start, end;

	private PathNode first, last, complete, completeReverse;

	private Queue<PathNode> openSet, closedSet, openSetReverse, closedSetReverse;
	private Map<BlockLocation, PathNode> nodeWorld, nodeWorldReverse;

	public AStarPathSearch(AStarPathSearchProvider provider, BlockLocation start, BlockLocation end) {
		this.provider = provider;
		this.start = start;
		this.end = end;

		heuristic = provider.getHeuristic();
		physics = provider.getWorldPhysics();

		nodeWorld = new HashMap<BlockLocation, PathNode>();
		first = new BlockPathNode(this, start);
		first.setCost(0);
		first.setCostEstimate(heuristic.calculateCost(start, end));
		openSet = new PriorityBlockingQueue<>(64, PATH_NODE_COMPARATOR);
		closedSet = new PriorityBlockingQueue<>(64, PATH_NODE_COMPARATOR);
		nodeWorld.put(start, first);
		openSet.offer(first);

		nodeWorldReverse = new HashMap<BlockLocation, PathNode>();
		last = new BlockPathNode(this, end);
		last.setCost(0);
		last.setCostEstimate(heuristic.calculateCost(end, start));
		openSetReverse = new PriorityBlockingQueue<>(64, PATH_NODE_COMPARATOR);
		closedSetReverse = new PriorityBlockingQueue<>(64, PATH_NODE_COMPARATOR);
		nodeWorldReverse.put(end, last);
		openSetReverse.offer(last);
	}

	@Override
	public void step() {
		if(isDone())
			return;

		PathNode current = openSet.poll();

		if(complete == null && current.getLocation().equals(end)) {
			complete = reconstructPath(current);
			return;
		}
		calculate(current, false);

		if(completeReverse != null)
			return;

		PathNode currentReverse = openSetReverse.poll();

		if(completeReverse == null && start.equals(currentReverse.getLocation()))
			completeReverse = reconstructPath(currentReverse);
		else if(completeReverse == null)
			calculate(currentReverse, true);
	}

	private void calculate(PathNode current, boolean reverse) {
		BlockLocation location = current.getLocation();

		Map<BlockLocation, PathNode> nodeWorld = (reverse ? nodeWorldReverse : this.nodeWorld);
		Queue<PathNode> openSet = (reverse ? openSetReverse : this.openSet);
		Queue<PathNode> closedSet = (reverse ? closedSetReverse : this.closedSet);

		closedSet.offer(current);
		for(BlockLocation adjacentLocation : physics.findAdjacent(current.getLocation())) {
			PathNode adjacent;
			if(!nodeWorld.containsKey(adjacentLocation)) {
				adjacent = new BlockPathNode(this, adjacentLocation);
				adjacent.setPrevious(current);
				nodeWorld.put(adjacentLocation, adjacent);
			} else
				adjacent = nodeWorld.get(adjacentLocation);

			if(closedSet.contains(adjacent))
				continue;
			if(!physics.canWalk(location, adjacentLocation))
				continue;
			if(reverse && !physics.canWalk(adjacentLocation, location))
				continue;

			double cost = current.getCost() + heuristic.calculateCost(location, adjacentLocation);

			boolean contained = openSet.contains(adjacent);
			if(!contained || cost < adjacent.getCost()) {
				if(!contained)
					openSet.offer(adjacent);
				adjacent.setPrevious(current);
				current.setNext(adjacent);
				adjacent.setCost(cost);
				adjacent.setCostEstimate(cost + heuristic.calculateCost(adjacentLocation, reverse ? start : end));
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
		return complete != null || openSet.isEmpty() || (openSetReverse.isEmpty() && completeReverse == null);
	}

	@Override
	public BlockLocation getStart() {
		return start;
	}

	@Override
	public BlockLocation getEnd() {
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

	private static final class PathNodeComparator implements Comparator<PathNode> {
		@Override
		public int compare(PathNode node1, PathNode node2) {
			return Double.compare(node1.getCostEstimate(), node2.getCostEstimate());
		}
	}
}
