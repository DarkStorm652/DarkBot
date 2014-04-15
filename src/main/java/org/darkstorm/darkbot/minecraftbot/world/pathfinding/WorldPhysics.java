package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public interface WorldPhysics {
	public BlockLocation[] findAdjacent(BlockLocation location);

	public boolean canWalk(BlockLocation from, BlockLocation to);
	public boolean canClimb(BlockLocation location);

	public World getWorld();
}
