package org.darkstorm.minecraft.darkbot.world.pathfinding;

import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public interface WorldPhysics {
	public BlockLocation[] findAdjacent(BlockLocation location);

	public boolean canWalk(BlockLocation from, BlockLocation to);
	public boolean canClimb(BlockLocation location);

	public World getWorld();
}
