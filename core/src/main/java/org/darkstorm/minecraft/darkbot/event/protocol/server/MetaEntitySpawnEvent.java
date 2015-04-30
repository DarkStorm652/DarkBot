package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

public abstract class MetaEntitySpawnEvent extends RotatedEntitySpawnEvent {
	private final IntHashMap<WatchableObject> metadata;

	public MetaEntitySpawnEvent(int entityId, RotatedSpawnLocation location, IntHashMap<WatchableObject> metadata) {
		super(entityId, location);

		this.metadata = metadata;
	}

	public IntHashMap<WatchableObject> getMetadata() {
		return metadata;
	}
}
