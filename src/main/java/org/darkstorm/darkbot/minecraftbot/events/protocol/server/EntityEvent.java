package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;

public abstract class EntityEvent extends ProtocolEvent {
	private final int entityId;

	public EntityEvent(int entityId) {
		this.entityId = entityId;
	}

	public int getEntityId() {
		return entityId;
	}
}
