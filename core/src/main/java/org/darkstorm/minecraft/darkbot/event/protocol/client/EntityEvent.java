package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;
import org.darkstorm.minecraft.darkbot.world.entity.Entity;

public abstract class EntityEvent extends AbstractEvent {
	private final Entity entity;

	public EntityEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
