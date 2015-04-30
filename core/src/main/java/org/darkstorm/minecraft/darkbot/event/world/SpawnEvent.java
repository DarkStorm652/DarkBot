package org.darkstorm.minecraft.darkbot.event.world;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public class SpawnEvent extends AbstractEvent {
	private final MainPlayerEntity player;

	public SpawnEvent(MainPlayerEntity player) {
		this.player = player;
	}

	public MainPlayerEntity getPlayer() {
		return player;
	}
}
