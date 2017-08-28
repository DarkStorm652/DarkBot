package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;

public class EntityMetadataUpdateEvent extends EntityEvent {
	private final EntityMetadata[] metadata;

	public EntityMetadataUpdateEvent(int entityId, EntityMetadata[] metadata) {
		super(entityId);

		this.metadata = metadata;
	}

	public EntityMetadata[] getMetadata() {
		return metadata;
	}
}
