package org.darkstorm.darkbot.minecraftbot.world.block;

public final class TileEntity {
	private final int x, y, z;

	public TileEntity(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
}
