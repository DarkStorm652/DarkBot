package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

public class EntityMoveEvent extends EntityEvent {
	private final double x, y, z;

	public EntityMoveEvent(int entityId, double x, double y, double z) {
		super(entityId);

		this.x = x;
		this.y = y;
		this.z = z;
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
}
