package org.darkstorm.darkbot.minecraftbot.event.protocol.client;

import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public abstract class PlayerEvent extends EntityEvent {
	public PlayerEvent(MainPlayerEntity entity) {
		super(entity);
	}

	@Override
	public MainPlayerEntity getEntity() {
		return (MainPlayerEntity) super.getEntity();
	}
}
