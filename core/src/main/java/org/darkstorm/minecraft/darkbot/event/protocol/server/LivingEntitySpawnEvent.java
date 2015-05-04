package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

public class LivingEntitySpawnEvent extends MetaEntitySpawnEvent {
	private final LivingEntitySpawnData spawnData;

	public LivingEntitySpawnEvent(int entityId, LivingEntitySpawnLocation location, LivingEntitySpawnData spawnData, IntHashMap<WatchableObject> metadata) {
		super(entityId, location, metadata);

		this.spawnData = spawnData;
	}

	@Override
	public LivingEntitySpawnLocation getLocation() {
		return (LivingEntitySpawnLocation) super.getLocation();
	}

	public LivingEntitySpawnData getSpawnData() {
		return spawnData;
	}

	public static class LivingEntitySpawnLocation extends RotatedSpawnLocation {
		private final double headYaw;

		public LivingEntitySpawnLocation(double x, double y, double z, double yaw, double pitch, double headYaw) {
			super(x, y, z, yaw, pitch);

			this.headYaw = headYaw;
		}

		public double getHeadYaw() {
			return headYaw;
		}
	}

	public static class LivingEntitySpawnData {
		private final int type;
		private final double velocityX, velocityY, velocityZ;

		public LivingEntitySpawnData(int type, double velocityX, double velocityY, double velocityZ) {
			this.type = type;
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.velocityZ = velocityZ;
		}

		public int getType() {
			return type;
		}

		public double getVelocityX() {
			return velocityX;
		}

		public double getVelocityY() {
			return velocityY;
		}

		public double getVelocityZ() {
			return velocityZ;
		}
	}
}
