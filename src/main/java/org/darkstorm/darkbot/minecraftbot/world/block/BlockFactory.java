package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.World;

public interface BlockFactory<T extends Block> {
	public T createBlock(World world, Chunk chunk, BlockLocation location, int metadata);
	public BlockType getType();
}
