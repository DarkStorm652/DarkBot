package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.world.entity.Entity;

public class EntityHitEvent extends EntityInteractEvent {
	public EntityHitEvent(Entity entity) {
		super(entity);
	}
}
