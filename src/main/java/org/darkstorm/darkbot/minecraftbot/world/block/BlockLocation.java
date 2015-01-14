package org.darkstorm.darkbot.minecraftbot.world.block;

import java.util.Comparator;

import org.darkstorm.darkbot.minecraftbot.world.WorldLocation;

public final class BlockLocation {
	public static final Comparator<BlockLocation> DISTANCE_COMPARATOR = new Comparator<BlockLocation>() {
		@Override
		public int compare(BlockLocation o1, BlockLocation o2) {
			return o1.equals(o2) ? 0 : o1.getDistanceToSquared(o2) < 0 ? -1 : 1;
		}
	};
	
	private final int x, y, z;

	public BlockLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockLocation(WorldLocation worldLocation) {
		this((int) Math.floor(worldLocation.getX()),
				(int) Math.floor(worldLocation.getY()),
				(int) Math.floor(worldLocation.getZ()));
	}

	public BlockLocation(ChunkLocation location) {
		this(location.getX() << 4, location.getY() << 4, location.getZ() << 4);
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

	public double getDistanceTo(BlockLocation other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
	}

	public long getDistanceToSquared(BlockLocation other) {
		return (long) (Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
	}

	public BlockLocation offset(BlockLocation location) {
		return offset(location.x, location.y, location.z);
	}
	
	public BlockLocation offsetInverse(BlockLocation location) {
		return offset(-location.x, -location.y, -location.z);
	}

	public BlockLocation offset(int x, int y, int z) {
		return new BlockLocation(this.x + x, this.y + y, this.z + z);
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
		if(!(obj instanceof BlockLocation))
			return false;
		BlockLocation location = (BlockLocation) obj;
		return x == location.getX() && y == location.getY() && z == location.getZ();
	}

	@Override
	public String toString() {
		return "BlockPos[" + x + "," + y + "," + z + "]";
	}
}
