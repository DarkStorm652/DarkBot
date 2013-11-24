package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;

public abstract class WindowEvent extends ProtocolEvent {
	private final int windowId;

	public WindowEvent(int windowId) {
		this.windowId = windowId;
	}

	public int getWindowId() {
		return windowId;
	}
}
