package org.darkstorm.minecraft.darkbot.world;

public enum Direction {
	NORTH(1, 0, 0),
	SOUTH(-1, 0, 0),
	EAST(0, 0, 1),
	WEST(0, 0, -1),
	NORTH_EAST(1, 0, 1),
	NORTH_WEST(1, 0, -1),
	SOUTH_EAST(-1, 0, 1),
	SOUTH_WEST(-1, 0, -1),
	
	UP(0, 1, 0),
	UPPER_NORTH(1, 1, 0),
	UPPER_SOUTH(-1, 1, 0),
	UPPER_EAST(0, 1, 1),
	UPPER_WEST(0, 1, -1),
	UPPER_NORTH_EAST(1, 1, 1),
	UPPER_NORTH_WEST(1, 1, -1),
	UPPER_SOUTH_EAST(-1, 1, 1),
	UPPER_SOUTH_WEST(-1, 1, -1),
	
	DOWN(0, -1, 0),
	LOWER_NORTH(1, -1, 0),
	LOWER_SOUTH(-1, -1, 0),
	LOWER_EAST(0, -1, 1),
	LOWER_WEST(0, -1, -1),
	LOWER_NORTH_EAST(1, -1, 1),
	LOWER_NORTH_WEST(1, -1, -1),
	LOWER_SOUTH_EAST(-1, -1, 1),
	LOWER_SOUTH_WEST(-1, -1, -1);
	
	private final int offX, offY, offZ;
	
	private final double horizAngle, vertAngle;
	
	private Direction(int offX, int offY, int offZ) {
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
		
		horizAngle = Math.atan2(offZ, offX);
		vertAngle = Math.atan2(offY, Math.hypot(offX, offZ));
	}
	
	public int getBlockOffsetX() {
		return offX;
	}
	
	public int getBlockOffsetY() {
		return offY;
	}
	
	public int getBlockOffsetZ() {
		return offZ;
	}
	
	public double getHorizontalAngle() {
		return horizAngle;
	}
	
	public double getVerticalAngle() {
		return vertAngle;
	}
}
