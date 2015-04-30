package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public abstract class PlayerEvent extends EntityEvent {
	public PlayerEvent(MainPlayerEntity entity) {
		super(entity);
	}

	@Override
	public MainPlayerEntity getEntity() {
		return (MainPlayerEntity) super.getEntity();
	}
}
