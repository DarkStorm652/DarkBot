package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.Direction;

public interface FenceGateBlock extends Block {
	public Direction getDirection();
	public boolean isOpen();
}
