package org.darkstorm.minecraft.darkbot.world.block;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;

public abstract class TileEntity {
	protected final BlockLocation location;

	public TileEntity(CompoundTag nbt) {
		this((int)nbt.get("x").getValue(), (int)nbt.get("y").getValue(), (int)nbt.get("z").getValue());
	}

	public TileEntity(int x, int y, int z) {
		this(new BlockLocation(x, y, z));
	}

	public TileEntity(BlockLocation location) {
		this.location = location;
	}

	public int getX() {
		return location.getX();
	}

	public int getY() {
		return location.getY();
	}

	public int getZ() {
		return location.getZ();
	}

	public BlockLocation getLocation() {
		return location;
	}
}
