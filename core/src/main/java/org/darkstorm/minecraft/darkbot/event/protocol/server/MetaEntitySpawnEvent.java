package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

public abstract class MetaEntitySpawnEvent extends RotatedEntitySpawnEvent {
	private final EntityMetadata[] metadata;

	public MetaEntitySpawnEvent(int entityId, RotatedSpawnLocation location, EntityMetadata[] metadata) {
		super(entityId, location);

		this.metadata = metadata;
	}

	public EntityMetadata[] getMetadata() {
		return metadata;
	}
}
