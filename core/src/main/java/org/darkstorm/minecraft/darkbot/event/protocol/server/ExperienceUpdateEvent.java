package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class ExperienceUpdateEvent extends ProtocolEvent {
	private final int experienceLevel, experienceTotal;

	public ExperienceUpdateEvent(int experienceLevel, int experienceTotal) {
		this.experienceLevel = experienceLevel;
		this.experienceTotal = experienceTotal;
	}

	public int getExperienceLevel() {
		return experienceLevel;
	}

	public int getExperienceTotal() {
		return experienceTotal;
	}
}
