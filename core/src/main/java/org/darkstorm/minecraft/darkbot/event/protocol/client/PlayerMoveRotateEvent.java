package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public class PlayerMoveRotateEvent extends PlayerUpdateEvent {
	public PlayerMoveRotateEvent(MainPlayerEntity entity) {
		super(entity);
	}

	public PlayerMoveRotateEvent(MainPlayerEntity entity, double x, double y, double z, float yaw, float pitch) {
		super(entity, x, y, z, yaw, pitch, entity.isOnGround());
	}

	public PlayerMoveRotateEvent(MainPlayerEntity entity, double x, double y, double z, float yaw, float pitch, boolean onGround) {
		super(entity, x, y, z, yaw, pitch, onGround);
	}
}
