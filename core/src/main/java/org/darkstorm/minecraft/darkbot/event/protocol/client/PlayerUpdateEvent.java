package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public class PlayerUpdateEvent extends PlayerEvent {
	private final double x, y, z;
	private final float yaw, pitch;
	private final boolean onGround;

	public PlayerUpdateEvent(MainPlayerEntity entity) {
		this(entity, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch(), entity.isOnGround());
	}

	public PlayerUpdateEvent(MainPlayerEntity entity, boolean onGround) {
		this(entity, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch(), onGround);
	}

	protected PlayerUpdateEvent(MainPlayerEntity entity, double x, double y, double z, float yaw, float pitch, boolean onGround) {
		super(entity);

		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
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

	public boolean isOnGround() {
		return onGround;
	}
}
