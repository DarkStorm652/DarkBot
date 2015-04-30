package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.Entity;

public abstract class EntityInteractEvent extends EntityEvent {
	public EntityInteractEvent(Entity entity) {
		super(entity);
	}
}
