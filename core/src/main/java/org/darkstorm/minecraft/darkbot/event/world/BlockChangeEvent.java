package org.darkstorm.minecraft.darkbot.event.world;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;
import org.darkstorm.minecraft.darkbot.world.*;
import org.darkstorm.minecraft.darkbot.world.block.*;

public class BlockChangeEvent extends AbstractEvent {
	private final World world;
	private final BlockLocation location;
	private final Block oldBlock, newBlock;

	public BlockChangeEvent(World world, BlockLocation location,
			Block oldBlock, Block newBlock) {
		this.world = world;
		this.location = location;
		this.oldBlock = oldBlock;
		this.newBlock = newBlock;
	}

	public World getWorld() {
		return world;
	}

	public BlockLocation getLocation() {
		return location;
	}

	public Block getOldBlock() {
		return oldBlock;
	}

	public Block getNewBlock() {
		return newBlock;
	}
}
