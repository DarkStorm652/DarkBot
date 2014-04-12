package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;

public class EntityMetadataUpdateEvent extends EntityEvent {
	private final IntHashMap<WatchableObject> metadata;

	public EntityMetadataUpdateEvent(int entityId, IntHashMap<WatchableObject> metadata) {
		super(entityId);

		this.metadata = metadata;
	}

	public IntHashMap<WatchableObject> getMetadata() {
		return metadata;
	}
}
