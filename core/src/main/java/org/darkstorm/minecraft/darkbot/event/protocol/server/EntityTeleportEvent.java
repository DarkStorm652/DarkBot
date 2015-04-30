package org.darkstorm.minecraft.darkbot.event.protocol.server;

public class EntityTeleportEvent extends EntityEvent {
	private final double x, y, z, yaw, pitch;

	public EntityTeleportEvent(int entityId, double x, double y, double z, double yaw, double pitch) {
		super(entityId);

		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
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

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}
}
