package org.darkstorm.darkbot.minecraftbot.events;

public abstract class Event {
	public String getName() {
		return getClass().getSimpleName();
	}
}
