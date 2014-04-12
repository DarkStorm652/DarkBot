package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

public abstract class EntitySpawnEvent extends EntityEvent {
	private final SpawnLocation location;

	public EntitySpawnEvent(int entityId, SpawnLocation location) {
		super(entityId);

		this.location = location;
	}

	public SpawnLocation getLocation() {
		return location;
	}

	public static class SpawnLocation {
		private final double x, y, z;

		public SpawnLocation(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
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
	}
}
