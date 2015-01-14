package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.World;

public interface BlockFactory {
	public Block createBlock(World world, Chunk chunk, BlockLocation location, int metadata);
	public BlockType getType();
}
