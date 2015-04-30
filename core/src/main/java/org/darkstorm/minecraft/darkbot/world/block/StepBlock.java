package org.darkstorm.minecraft.darkbot.world.block;

public interface StepBlock extends Block {
	public enum Material {
		STONE, SANDSTONE, STONE_WOOD, COBBLESTONE, BRICK, STONE_BRICK, NETHER_BRICK, QUARTZ,
		RED_SANDSTONE, OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK
	}
	public Material getMaterial();
	public boolean isUpper();
}
