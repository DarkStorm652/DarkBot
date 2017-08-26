package org.darkstorm.minecraft.darkbot.event.protocol.server;

public class EntityHeadRotateEvent extends EntityEvent {
	private final float headYaw;

	public EntityHeadRotateEvent(int entityId, float headYaw) {
		super(entityId);

		this.headYaw = headYaw;
	}

	public float getHeadYaw() {
		return headYaw;
	}
}
