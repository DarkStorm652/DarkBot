package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

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
