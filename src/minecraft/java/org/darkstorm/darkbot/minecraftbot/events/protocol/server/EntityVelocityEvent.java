package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

public class EntityVelocityEvent extends EntityEvent {
	private final double velocityX, velocityY, velocityZ;

	public EntityVelocityEvent(int entityId, double velocityX, double velocityY, double velocityZ) {
		super(entityId);

		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
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
