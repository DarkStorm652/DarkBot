package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public class PlayerMoveEvent extends PlayerUpdateEvent {
	public PlayerMoveEvent(MainPlayerEntity entity) {
		super(entity);
	}

	public PlayerMoveEvent(MainPlayerEntity entity, double x, double y, double z) {
		super(entity, x, y, z, entity.getYaw(), entity.getPitch(), entity.isOnGround());
	}

	public PlayerMoveEvent(MainPlayerEntity entity, double x, double y, double z, boolean onGround) {
		super(entity, x, y, z, entity.getYaw(), entity.getPitch(), onGround);
	}
}
