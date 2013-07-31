package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

public class VehicleSpawnEvent extends RotatedEntitySpawnEvent {
	private final VehicleSpawnData spawnData;

	public VehicleSpawnEvent(int entityId, RotatedSpawnLocation location, VehicleSpawnData spawnData) {
		super(entityId, location);

		this.spawnData = spawnData;
	}

	public VehicleSpawnData getSpawnData() {
		return spawnData;
	}

	public static class VehicleSpawnData {
		private final int type;

		public VehicleSpawnData(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	public static class ThrownVehicleSpawnData extends VehicleSpawnData {
		private final int throwerId;
		private final double speedX, speedY, speedZ;

		public ThrownVehicleSpawnData(int type, int throwerId, double speedX, double speedY, double speedZ) {
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
