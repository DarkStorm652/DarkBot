package org.darkstorm.darkbot.minecraftbot.world;

import org.darkstorm.darkbot.minecraftbot.world.block.*;

public final class WorldLocation {
	private final double x, y, z;

	public WorldLocation(double x, double y, double z) {
		this.x = x + 0.0;
		this.y = y + 0.0;
		this.z = z + 0.0;
	}

	public WorldLocation(BlockLocation location) {
		x = location.getX() + 0.5;
		y = location.getY();
		z = location.getZ() + 0.5;
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

	public double getDistanceTo(WorldLocation other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
	}

	public double getDistanceToSquared(WorldLocation other) {
		return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2);
	}

	public WorldLocation offset(WorldLocation location) {
		return offset(location.x, location.y, location.z);
	}

	public WorldLocation offset(double x, double y, double z) {
		return new WorldLocation(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof WorldLocation))
			return false;
		WorldLocation location = (WorldLocation) obj;
		return x == location.getX() && y == location.getY() && z == location.getZ();
	}

	@Override
	public String toString() {
		return "WorldPos[" + x + "," + y + "," + z + "]";
	}
}
