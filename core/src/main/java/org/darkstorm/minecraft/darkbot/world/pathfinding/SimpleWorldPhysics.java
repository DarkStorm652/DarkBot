package org.darkstorm.minecraft.darkbot.world.pathfinding;

import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.*;

public class SimpleWorldPhysics implements WorldPhysics {
	private static final BlockLocation[] surrounding = new BlockLocation[] {
			// middle y + 0
			new BlockLocation(-1, 0, 1), new BlockLocation(0, 0, 1), new BlockLocation(1, 0, 1), new BlockLocation(-1, 0, 0), new BlockLocation(1, 0, 0),
			new BlockLocation(-1, 0, -1), new BlockLocation(0, 0, -1),
			new BlockLocation(1, 0, -1),
			// bottom y - 1
			new BlockLocation(-1, -1, 1), new BlockLocation(0, -1, 1), new BlockLocation(1, -1, 1), new BlockLocation(-1, -1, 0), new BlockLocation(0, -1, 0),
			new BlockLocation(1, -1, 0), new BlockLocation(-1, -1, -1), new BlockLocation(0, -1, -1), new BlockLocation(1, -1, -1),
			// top y + 1
			new BlockLocation(-1, 1, 1), new BlockLocation(0, 1, 1), new BlockLocation(1, 1, 1), new BlockLocation(-1, 1, 0), new BlockLocation(0, 1, 0),
			new BlockLocation(1, 1, 0), new BlockLocation(-1, 1, -1), new BlockLocation(0, 1, -1), new BlockLocation(1, 1, -1), };
	private static final boolean[] emptyBlocks;

	static {
		emptyBlocks = new boolean[256];
		for(BlockType type : BlockType.values())
			if(type.getId() >= 0 && type.getId() < 256)
				emptyBlocks[type.getId()] = !type.isSolid();
	}

	private final World world;

	public SimpleWorldPhysics(World world) {
		this.world = world;
	}

	@Override
	public BlockLocation[] findAdjacent(BlockLocation location) {
		BlockLocation[] locations = new BlockLocation[surrounding.length];
		for(int i = 0; i < locations.length; i++)
			locations[i] = location.offset(surrounding[i]);
		return locations;
	}

	@Override
	public boolean canWalk(BlockLocation location, BlockLocation location2) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		int x2 = location2.getX(), y2 = location2.getY(), z2 = location2.getZ();
		if(y2 < 0)
			return false;

		boolean valid = true;
		/*valid = valid && !collides(x2, y2, z2);
		valid = valid && !collidesBetween(x, y, z, x2, y2, z2);
		
		if(valid) {
			boolean curInAir = isGrounded(x, y, z);
			boolean nextInAir = isGrounded(x2, y2, z2);
		}*/
		
		
		
		
		
		valid = valid && isEmpty(x2, y2, z2); // Block at must be non-solid
		valid = valid && isEmpty(x2, y2 + 1, z2); // Block above must be non-solid

		int lowerBlock = world.getBlockIdAt(x2, y2 - 1, z2);
		valid = valid && lowerBlock != 10;
		valid = valid && lowerBlock != 11;

		if(isEmpty(x, y - 1, z))
			valid = valid
					&& ((y2 < y && x2 == x && z2 == z)
							|| ((canClimb(location) && canClimb(location2)) || (!canClimb(location) && canClimb(location2)) || (canClimb(location)
									&& !canClimb(location2) && (x2 == x && z2 == z ? true : !isEmpty(x2, y2 - 1, z2)))) || !isEmpty(x2, y2 - 1, z2));
		if(y != y2 && (x != x2 || z != z2))
			return false;
		if(x != x2 && z != z2) {
			valid = valid && isEmpty(x2, y, z);
			valid = valid && isEmpty(x, y, z2);
			valid = valid && isEmpty(x2, y + 1, z);
			valid = valid && isEmpty(x, y + 1, z2);
			if(y != y2) {
				valid = valid && isEmpty(x2, y2, z);
				valid = valid && isEmpty(x, y2, z2);
				valid = valid && isEmpty(x, y2, z);
				valid = valid && isEmpty(x2, y, z2);
				valid = valid && isEmpty(x2, y + 1, z2);
				valid = valid && isEmpty(x, y2 + 1, z);
				valid = false;
			}
		} else if(x != x2 && y != y2) {
			valid = valid && isEmpty(x2, y, z);
			valid = valid && isEmpty(x, y2, z);
			if(y > y2)
				valid = valid && isEmpty(x2, y + 1, z);
			else
				valid = valid && isEmpty(x, y2 + 1, z);
			valid = false;
		} else if(z != z2 && y != y2) {
			valid = valid && isEmpty(x, y, z2);
			valid = valid && isEmpty(x, y2, z);
			if(y > y2)
				valid = valid && isEmpty(x, y + 1, z2);
			else
				valid = valid && isEmpty(x, y2 + 1, z);
			valid = false;
		}
		int nodeBlockUnder = world.getBlockIdAt(x2, y2 - 1, z2);
		if(nodeBlockUnder == 107 || nodeBlockUnder == 113)
			valid = false;
		return valid;
	}

	@Override
	public boolean canClimb(BlockLocation location) {
		int id = world.getBlockIdAt(location);
		if(id == 8 || id == 9 || id == 65) // Water / Moving Water / Ladder
			return true;
		if(id == 106) { // Vines (which require an adjacent solid block)
			if(!isEmpty(location.getX(), location.getY(), location.getZ() + 1) || !isEmpty(location.getX(), location.getY(), location.getZ() - 1)
					|| !isEmpty(location.getX() + 1, location.getY(), location.getZ()) || !isEmpty(location.getX() - 1, location.getY(), location.getZ()))
				return true;
		}
		return false;
	}
	
	private boolean isGrounded(int x, int y, int z) {
		return collidesExact(x + 0.5, y - 0.1, z + 0.5);
	}
	
	private boolean collides(int x, int y, int z) {
		return collidesExact(x + 0.5, y, z + 0.5);
	}
	
	private boolean collidesBetween(int x1, int y1, int z1, int x2, int y2, int z2) {
		return collidesExact((x1 + 0.5) + (x2 - x1) / 2.0, y1 + (y2 - y1) / 2.0, (z1 + 0.5) + (z2 - z1) / 2.0);
	}
	
	private boolean collidesExact(double x, double y, double z) {
		return world.isColliding(world.getBot().getPlayer().getBoundingBoxAt(x + 0.5, y, z + 0.5));
	}

	private boolean isEmpty(int x, int y, int z) {
		int id = world.getBlockIdAt(x, y, z);
		return id >= 0 && id < emptyBlocks.length && emptyBlocks[id];
	}

	@Override
	public World getWorld() {
		return world;
	}
}
