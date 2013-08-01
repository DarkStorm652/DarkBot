package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.nbt.NBTTagCompound;

public abstract class TileEntity {
	protected final BlockLocation location;

	public TileEntity(NBTTagCompound nbt) {
		this(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
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
