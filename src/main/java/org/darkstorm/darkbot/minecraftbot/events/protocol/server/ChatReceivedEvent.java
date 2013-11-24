package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;

public class ChatReceivedEvent extends ProtocolEvent {
	private final String message;

	public ChatReceivedEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
