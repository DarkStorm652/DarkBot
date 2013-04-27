package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class PingMessage extends Message {
	private String message;

	public PingMessage(MessageType type, String raw, String message) {
		super(type, raw);
		if(!type.equals(MessageType.PING) && !type.equals(MessageType.PONG))
			throw new IllegalArgumentException("type is not PING or PONG");
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
