package org.darkstorm.darkbot.minecraftbot.events.protocol.server;


public abstract class RotatedEntitySpawnEvent extends EntitySpawnEvent {
	public RotatedEntitySpawnEvent(int entityId, RotatedSpawnLocation location) {
		super(entityId, location);
	}

	@Override
	public RotatedSpawnLocation getLocation() {
		return (RotatedSpawnLocation) super.getLocation();
	}

	public static class RotatedSpawnLocation extends SpawnLocation {
		private final double yaw, pitch;

		public RotatedSpawnLocation(double x, double y, double z, double yaw, double pitch) {
			super(x, y, z);

			this.yaw = yaw;
			this.pitch = pitch;
		}

		public double getYaw() {
			return yaw;
		}

		public double getPitch() {
			return pitch;
		}
	}
}
