package org.darkstorm.minecraft.darkbot.event.protocol.client;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class ChatSentEvent extends ProtocolEvent {
	private final String message;

	public ChatSentEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
