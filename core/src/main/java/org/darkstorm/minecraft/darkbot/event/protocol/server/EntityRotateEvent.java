package org.darkstorm.minecraft.darkbot.event.protocol.server;

public class EntityRotateEvent extends EntityEvent {
	private final double yaw, pitch;

	public EntityRotateEvent(int entityId, double yaw, double pitch) {
		super(entityId);

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
