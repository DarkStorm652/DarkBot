package org.darkstorm.minecraft.darkbot.event.protocol.server;

public class EntityTeleportEvent extends EntityEvent {
	private final double x, y, z;
	private final float yaw, pitch;

	public EntityTeleportEvent(int entityId, double x, double y, double z, float yaw, float pitch) {
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

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}
}
