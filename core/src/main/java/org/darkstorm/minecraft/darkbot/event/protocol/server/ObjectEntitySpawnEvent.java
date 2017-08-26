package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.type.object.ObjectType;

public class ObjectEntitySpawnEvent extends RotatedEntitySpawnEvent {
	private final ObjectSpawnData spawnData;

	public ObjectEntitySpawnEvent(int entityId, RotatedSpawnLocation location, ObjectSpawnData spawnData) {
		super(entityId, location);

		this.spawnData = spawnData;
	}

	public ObjectSpawnData getSpawnData() {
		return spawnData;
	}

	public static class ObjectSpawnData {
		private final ObjectType type;

		public ObjectSpawnData(ObjectType type) {
			this.type = type;
		}

		public ObjectType getType() {
			return type;
		}
	}

	public static class ThrownObjectSpawnData extends ObjectSpawnData {
		private final int throwerId;
		private final double speedX, speedY, speedZ;

		public ThrownObjectSpawnData(ObjectType type, int throwerId, double speedX, double speedY, double speedZ) {
			super(type);

			this.throwerId = throwerId;
			this.speedX = speedX;
			this.speedY = speedY;
			this.speedZ = speedZ;
		}

		public int getThrowerId() {
			return throwerId;
		}

		public double getSpeedX() {
			return speedX;
		}

		public double getSpeedY() {
			return speedY;
		}

		public double getSpeedZ() {
			return speedZ;
		}
	}
}
