package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

public class EntityHeadRotateEvent extends EntityEvent {
	private final double headYaw;

	public EntityHeadRotateEvent(int entityId, double headYaw) {
		super(entityId);

		this.headYaw = headYaw;
	}

	public double getHeadYaw() {
		return headYaw;
	}
}
