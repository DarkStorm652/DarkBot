package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;

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
