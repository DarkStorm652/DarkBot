package org.darkstorm.minecraft.darkbot.world.block;

import org.darkstorm.minecraft.darkbot.world.Direction;

public interface FenceBlock extends Block {
	public boolean isConnected(Direction direction);
}
