package org.darkstorm.darkbot.minecraftbot.event.world;

import org.darkstorm.darkbot.minecraftbot.event.AbstractEvent;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class SpawnEvent extends AbstractEvent {
	private final MainPlayerEntity player;

	public SpawnEvent(MainPlayerEntity player) {
		this.player = player;
	}

	public MainPlayerEntity getPlayer() {
		return player;
	}
}
