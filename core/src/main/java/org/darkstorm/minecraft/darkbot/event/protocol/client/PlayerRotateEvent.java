package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public class PlayerRotateEvent extends PlayerUpdateEvent {
	public PlayerRotateEvent(MainPlayerEntity entity) {
		super(entity);
	}

	public PlayerRotateEvent(MainPlayerEntity entity, float yaw, float pitch) {
		super(entity, entity.getX(), entity.getY(), entity.getZ(), yaw, pitch, entity.isOnGround());
	}

	public PlayerRotateEvent(MainPlayerEntity entity, float yaw, float pitch, boolean onGround) {
		super(entity, entity.getX(), entity.getY(), entity.getZ(), yaw, pitch, onGround);
	}
}
