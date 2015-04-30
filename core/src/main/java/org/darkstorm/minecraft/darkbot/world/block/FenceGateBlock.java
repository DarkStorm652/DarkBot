package org.darkstorm.minecraft.darkbot.world.block;

import org.darkstorm.minecraft.darkbot.world.Direction;

public interface FenceGateBlock extends Block {
	public Direction getDirection();
	public boolean isOpen();
}
