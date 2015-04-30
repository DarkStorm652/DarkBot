package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.Entity;

public class EntityUseEvent extends EntityInteractEvent {
	public EntityUseEvent(Entity entity) {
		super(entity);
	}
}
