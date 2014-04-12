package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.event.protocol.ProtocolEvent;

public class ChatReceivedEvent extends ProtocolEvent {
	private final String message;

	public ChatReceivedEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
