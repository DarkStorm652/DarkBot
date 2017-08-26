package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

public class LivingEntitySpawnEvent extends MetaEntitySpawnEvent {
	private final LivingEntitySpawnData spawnData;

	public LivingEntitySpawnEvent(int entityId, LivingEntitySpawnLocation location, LivingEntitySpawnData spawnData, EntityMetadata[] metadata) {
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
		private final float headYaw;

		public LivingEntitySpawnLocation(double x, double y, double z, float yaw, float pitch, float headYaw) {
			super(x, y, z, yaw, pitch);

			this.headYaw = headYaw;
		}

		public float getHeadYaw() {
			return headYaw;
		}
	}

	public static class LivingEntitySpawnData {
		private final MobType type;
		private final double velocityX, velocityY, velocityZ;

		public LivingEntitySpawnData(MobType type, double velocityX, double velocityY, double velocityZ) {
			this.type = type;
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.velocityZ = velocityZ;
		}

		public MobType getType() {
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
