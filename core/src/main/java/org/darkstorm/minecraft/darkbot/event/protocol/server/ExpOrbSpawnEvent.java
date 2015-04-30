package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

public class ExpOrbSpawnEvent extends EntitySpawnEvent {
	private final int expValue;

	public ExpOrbSpawnEvent(int entityId, SpawnLocation location, int expValue) {
		super(entityId, location);

		this.expValue = expValue;
	}

	public int getExpValue() {
		return expValue;
	}
}
