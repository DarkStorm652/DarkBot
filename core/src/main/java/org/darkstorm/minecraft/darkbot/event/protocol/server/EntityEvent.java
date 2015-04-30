package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public abstract class EntityEvent extends ProtocolEvent {
	private final int entityId;

	public EntityEvent(int entityId) {
		this.entityId = entityId;
	}

	public int getEntityId() {
		return entityId;
	}
}
