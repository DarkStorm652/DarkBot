package org.darkstorm.minecraft.darkbot.world.block;

import org.darkstorm.minecraft.darkbot.world.Direction;

public interface StairBlock extends Block {
	public enum Material {
		SANDSTONE, COBBLESTONE, BRICK, STONE_BRICK, NETHER_BRICK, QUARTZ, RED_SANDSTONE,
		OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK
	}
	public Material getMaterial();
	public Direction getDirection();
	public boolean isUpsideDown();
}
