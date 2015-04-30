package org.darkstorm.minecraft.darkbot.world.block;

import org.darkstorm.minecraft.darkbot.world.World;

public interface BlockFactory<T extends Block> {
	public T createBlock(World world, Chunk chunk, BlockLocation location, int metadata);
	public BlockType getType();
}
