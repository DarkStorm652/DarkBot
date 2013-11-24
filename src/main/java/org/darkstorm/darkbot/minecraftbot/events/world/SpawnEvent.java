package org.darkstorm.darkbot.minecraftbot.events.world;

import org.darkstorm.darkbot.minecraftbot.events.Event;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class SpawnEvent extends Event {
	private final MainPlayerEntity player;

	public SpawnEvent(MainPlayerEntity player) {
		this.player = player;
	}

	public MainPlayerEntity getPlayer() {
		return player;
	}
}
