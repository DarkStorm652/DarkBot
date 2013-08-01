package org.darkstorm.darkbot.minecraftbot.world.pathfinding.astar;

import java.util.List;

import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.*;

public class BasicAStarHeuristic implements AStarHeuristic {
	private static final WorldLocation[] surrounding = new WorldLocation[] {
			// middle y + 0
			new WorldLocation(-1, 0, 1), new WorldLocation(0, 0, 1), new WorldLocation(1, 0, 1), new WorldLocation(-1, 0, 0), new WorldLocation(1, 0, 0), new WorldLocation(-1, 0, -1), new WorldLocation(0, 0, -1), new WorldLocation(1, 0, -1),
			// bottom y - 1
			new WorldLocation(-1, -1, 1), new WorldLocation(0, -1, 1), new WorldLocation(1, -1, 1), new WorldLocation(-1, -1, 0), new WorldLocation(0, -1, 0), new WorldLocation(1, -1, 0), new WorldLocation(-1, -1, -1), new WorldLocation(0, -1, -1), new WorldLocation(1, -1, -1),
			// top y + 1
			new WorldLocation(-1, 1, 1), new WorldLocation(0, 1, 1), new WorldLocation(1, 1, 1), new WorldLocation(-1, 1, 0), new WorldLocation(0, 1, 0), new WorldLocation(1, 1, 0), new WorldLocation(-1, 1, -1), new WorldLocation(0, 1, -1), new WorldLocation(1, 1, -1), };
	private static final boolean[] emptyBlocks;

	static {

		emptyBlocks = new boolean[256];
		for(BlockType type : BlockType.values())
			if(type.getId() >= 0 && type.getId() < 256)
				emptyBlocks[type.getId()] = !type.isSolid();
	}

	private final PathSearchProvider provider;
	private final World world;

	public BasicAStarHeuristic(PathSearchProvider provider) {
		this.provider = provider;
		world = provider.getWorld();
	}

	@Override
	public WorldLocation[] getSurrounding(AStarPathSearch search, WorldLocation location) {
		WorldLocation[] locations = new WorldLocation[surrounding.length];
		for(int i = 0; i < locations.length; i++)
			locations[i] = location.offset(surrounding[i]);
		return locations;
	}

	@Override
	public double calculateGScore(AStarPathSearch search, PathNode node, boolean reverse) {
		return node.isStart() ? 0 : node.getPrevious().getGScore() + node.getLocation().getDistanceTo(node.getPrevious().getLocation());
	}

	@Override
	public double calculateFScore(AStarPathSearch search, PathNode node, boolean reverse) {
		return node.getLocation().getDistanceTo(reverse ? search.getStart() : search.getEnd());
	}

	@Override
	public boolean isWalkable(PathNode current, PathNode node) {
		BlockLocation location = new BlockLocation(current.getLocation());
		int x = location.getX(), y = location.getY(), z = location.getZ();
		BlockLocation location2 = new BlockLocation(node.getLocation());
		int x2 = location2.getX(), y2 = location2.getY(), z2 = location2.getZ();
		if(y2 < 0)
			return false;

		boolean valid = true;
		valid &= emptyBlocks[world.getBlockIdAt(x2, y2, z2)];
		valid &= emptyBlocks[world.getBlockIdAt(x2, y2 + 1, z2)];
		int lowerBlock = world.getBlockIdAt(x2, y2 - 1, z2);
		valid &= lowerBlock != 10;
		valid &= lowerBlock != 11;
		int currentLowerBlock = world.getBlockIdAt(x, y - 1, z);
		if(emptyBlocks[currentLowerBlock])
			valid &= (y2 < y && x2 == x && z2 == z) || ((isClimbableBlock(location) && isClimbableBlock(location2)) || (!isClimbableBlock(location) && isClimbableBlock(location2)) || (isClimbableBlock(location) && !isClimbableBlock(location2) && (x2 == x && z2 == z ? true : !emptyBlocks[world.getBlockIdAt(x2, y2 - 1, z2)]))) || !emptyBlocks[world.getBlockIdAt(x2, y2 - 1, z2)];
		if(y != y2 && (x != x2 || z != z2))
			return false;
		if(x != x2 && z != z2) {
			valid &= emptyBlocks[world.getBlockIdAt(x2, y, z)];
			valid &= emptyBlocks[world.getBlockIdAt(x, y, z2)];
			valid &= emptyBlocks[world.getBlockIdAt(x2, y + 1, z)];
			valid &= emptyBlocks[world.getBlockIdAt(x, y + 1, z2)];
			if(y != y2) {
				valid &= emptyBlocks[world.getBlockIdAt(x2, y2, z)];
				valid &= emptyBlocks[world.getBlockIdAt(x, y2, z2)];
				valid &= emptyBlocks[world.getBlockIdAt(x, y2, z)];
				valid &= emptyBlocks[world.getBlockIdAt(x2, y, z2)];
				valid &= emptyBlocks[world.getBlockIdAt(x2, y + 1, z2)];
				valid &= emptyBlocks[world.getBlockIdAt(x, y2 + 1, z)];
				valid = false;
			}
		} else if(x != x2 && y != y2) {
			valid &= emptyBlocks[world.getBlockIdAt(x2, y, z)];
			valid &= emptyBlocks[world.getBlockIdAt(x, y2, z)];
			if(y > y2)
				valid &= emptyBlocks[world.getBlockIdAt(x2, y + 1, z)];
			else
				valid &= emptyBlocks[world.getBlockIdAt(x, y2 + 1, z)];
			valid = false;
		} else if(z != z2 && y != y2) {
			valid &= emptyBlocks[world.getBlockIdAt(x, y, z2)];
			valid &= emptyBlocks[world.getBlockIdAt(x, y2, z)];
			if(y > y2)
				valid &= emptyBlocks[world.getBlockIdAt(x, y + 1, z2)];
			else
				valid &= emptyBlocks[world.getBlockIdAt(x, y2 + 1, z)];
			valid = false;
		}
		int nodeBlockUnder = world.getBlockIdAt(x2, y2 - 1, z2);
		if(nodeBlockUnder == 85 || nodeBlockUnder == 107 || nodeBlockUnder == 113)
			valid = false;
		return valid;
	}

	@Override
	public boolean isClimbableBlock(BlockLocation location) {
		int id = world.getBlockIdAt(location);
		if(id == 8 || id == 9 || id == 65)
			return true;
		if(id == 106) {
			if(!isEmptyBlock(world.getBlockIdAt(location.getX(), location.getY(), location.getZ() + 1)) || !isEmptyBlock(world.getBlockIdAt(location.getX(), location.getY(), location.getZ() - 1)) || !isEmptyBlock(world.getBlockIdAt(location.getX() + 1, location.getY(), location.getZ())) || !isEmptyBlock(world.getBlockIdAt(location.getX() - 1, location.getY(), location.getZ())))
				return true;
		}
		return false;
	}

	private boolean isEmptyBlock(int id) {
		if(id < 0)
			return true;
		return emptyBlocks[id];
	}

	@Override
	public PathNode findNext(List<PathNode> openSet) {
		PathNode best = null;
		double bestF = 0;
		for(PathNode node : openSet) {
			double f = node.getFScore() + node.getGScore();
			if(best == null || f < bestF) {
				best = node;
				bestF = f;
			}
		}
		return best;
	}

	@Override
	public PathSearchProvider getProvider() {
		return provider;
	}
}
