package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class TimeUpdateEvent extends ProtocolEvent {
	private final long time, worldAge;

	public TimeUpdateEvent(long time, long worldAge) {
		this.time = time;
		this.worldAge = worldAge;
	}

	public long getTime() {
		return time;
	}

	public long getWorldAge() {
		return worldAge;
	}
}
