package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class BlockPlaceEvent extends BlockFaceEvent {
	private final ItemStack item;
	private final float xOffset, yOffset, zOffset;

	public BlockPlaceEvent(ItemStack item, BlockLocation location, int face) {
		this(item, location, face, 0F, 0F, 0F);
	}

	public BlockPlaceEvent(ItemStack item, BlockLocation location, int face, float xOffset, float yOffset, float zOffset) {
		super(location, face);

		this.item = item;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}

	public BlockPlaceEvent(ItemStack item, int x, int y, int z, int face) {
		this(item, x, y, z, face, 0F, 0F, 0F);
	}

	public BlockPlaceEvent(ItemStack item, int x, int y, int z, int face, float xOffset, float yOffset, float zOffset) {
		super(x, y, z, face);

		this.item = item;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}

	public ItemStack getItem() {
		return item;
	}

	public float getXOffset() {
		return xOffset;
	}

	public float getYOffset() {
		return yOffset;
	}

	public float getZOffset() {
		return zOffset;
	}
}
