package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class ChatReceivedEvent extends ProtocolEvent {
	private final String message;

	public ChatReceivedEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
