package org.darkstorm.minecraft.darkbot.event;

public abstract class AbstractEvent implements Event {
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
}
