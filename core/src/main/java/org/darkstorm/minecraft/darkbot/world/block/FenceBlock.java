package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.Direction;

public interface FenceBlock extends Block {
	public boolean isConnected(Direction direction);
}
