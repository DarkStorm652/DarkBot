package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

public class EntityCollectEvent extends EntityEvent {
	private final int collectedId, collectorId;

	public EntityCollectEvent(int collectedId, int collectorId) {
		super(collectedId);

		this.collectedId = collectedId;
		this.collectorId = collectorId;
	}

	public int getCollectedId() {
		return collectedId;
	}

	public int getCollectorId() {
		return collectorId;
	}
}
