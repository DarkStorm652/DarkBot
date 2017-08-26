package org.darkstorm.minecraft.darkbot.event.protocol.server;

public class EntityRotateEvent extends EntityEvent {
	private final float yaw, pitch;

	public EntityRotateEvent(int entityId, float yaw, float pitch) {
		super(entityId);

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
