package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class HealthUpdateEvent extends ProtocolEvent {
	private final float health;
	private final int hunger;
	private final float hungerSaturation;

	public HealthUpdateEvent(float health, int hunger, float hungerSaturation) {
		this.health = health;
		this.hunger = hunger;
		this.hungerSaturation = hungerSaturation;
	}

	public float getHealth() {
		return health;
	}

	public int getHunger() {
		return hunger;
	}

	public float getHungerSaturation() {
		return hungerSaturation;
	}
}
