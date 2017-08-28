package org.darkstorm.minecraft.darkbot.event.protocol.server;


public abstract class RotatedEntitySpawnEvent extends EntitySpawnEvent {
	public RotatedEntitySpawnEvent(int entityId, RotatedSpawnLocation location) {
		super(entityId, location);
	}

	@Override
	public RotatedSpawnLocation getLocation() {
		return (RotatedSpawnLocation) super.getLocation();
	}

	public static class RotatedSpawnLocation extends SpawnLocation {
		private final float yaw, pitch;

		public RotatedSpawnLocation(double x, double y, double z, float yaw, float pitch) {
			super(x, y, z);

			this.yaw = yaw;
			this.pitch = pitch;
		}

		public float getYaw() {
			return yaw;
		}

		public float getPitch() {
			return pitch;
		}
	}
}
