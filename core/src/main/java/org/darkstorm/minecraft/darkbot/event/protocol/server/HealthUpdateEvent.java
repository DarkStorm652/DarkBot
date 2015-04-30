package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class HealthUpdateEvent extends ProtocolEvent {
	private final int health, hunger;
	private final float hungerSaturation;

	public HealthUpdateEvent(int health, int hunger, float hungerSaturation) {
		this.health = health;
		this.hunger = hunger;
		this.hungerSaturation = hungerSaturation;
	}

	public int getHealth() {
		return health;
	}

	public int getHunger() {
		return hunger;
	}

	public float getHungerSaturation() {
		return hungerSaturation;
	}
}
