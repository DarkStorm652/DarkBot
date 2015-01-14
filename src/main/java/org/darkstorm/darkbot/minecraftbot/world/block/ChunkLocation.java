package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.WorldLocation;

public final class ChunkLocation {
	private final int x, y, z;

	public ChunkLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public ChunkLocation(BlockLocation location) {
		this(location.getX() >> 4, location.getY() >> 4, location.getZ() >> 4);
	}

	public ChunkLocation(WorldLocation worldLocation) {
		this((int) (worldLocation.getX()) >> 4,
				(int) (worldLocation.getY()) >> 4,
				(int) (worldLocation.getZ()) >> 4);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ChunkLocation))
			return false;
		ChunkLocation location = (ChunkLocation) obj;
		return x == location.getX() && y == location.getY() && z == location.getZ();
	}

	@Override
	public String toString() {
		return "ChunkPos[" + x + "," + y + "," + z + "]";
	}
}
