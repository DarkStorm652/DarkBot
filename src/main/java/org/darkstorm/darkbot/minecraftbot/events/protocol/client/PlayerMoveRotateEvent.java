package org.darkstorm.darkbot.minecraftbot.events.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class PlayerMoveRotateEvent extends PlayerUpdateEvent {
	public PlayerMoveRotateEvent(MainPlayerEntity entity) {
		super(entity);
	}

	public PlayerMoveRotateEvent(MainPlayerEntity entity, double x, double y, double z, double yaw, double pitch) {
		super(entity, x, y, z, yaw, pitch, entity.isOnGround());
	}

	public PlayerMoveRotateEvent(MainPlayerEntity entity, double x, double y, double z, double yaw, double pitch, boolean onGround) {
		super(entity, x, y, z, yaw, pitch, onGround);
	}
}
