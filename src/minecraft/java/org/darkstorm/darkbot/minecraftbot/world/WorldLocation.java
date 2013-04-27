package org.darkstorm.darkbot.minecraftbot.world;

import org.darkstorm.darkbot.minecraftbot.world.block.*;

public final class WorldLocation {
	private final double x, y, z;

	public WorldLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldLocation(BlockLocation location) {
		x = location.getX();
		y = location.getY();
		z = location.getZ();
	}

	public WorldLocation(ChunkLocation location) {
		x = location.getX() * 16;
		y = location.getY() * 16;
		z = location.getZ() * 16;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof WorldLocation))
			return false;
		WorldLocation location = (WorldLocation) obj;
		return location.getX() == x && location.getY() == y
				&& location.getZ() == z;
	}

	@Override
	public String toString() {
		return "WorldLocation[" + x + "," + y + "," + z + "]";
	}

	public double getDistanceTo(WorldLocation other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2)
				+ Math.pow(z - other.z, 2));
	}

	public double getDistanceToSquared(WorldLocation other) {
		return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2)
				+ Math.pow(z - other.z, 2);
	}

	public WorldLocation offset(WorldLocation location) {
		return offset(location.x, location.y, location.z);
	}

	public WorldLocation offset(double x, double y, double z) {
		return new WorldLocation(this.x + x, this.y + y, this.z + z);
	}
}
