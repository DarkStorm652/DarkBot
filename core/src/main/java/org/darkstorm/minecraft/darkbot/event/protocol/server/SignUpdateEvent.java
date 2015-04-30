package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class SignUpdateEvent extends ProtocolEvent {
	private final int x, y, z;
	private final String[] text;

	public SignUpdateEvent(int x, int y, int z, String[] text) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.text = text;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String[] getText() {
		return text.clone();
	}
}
