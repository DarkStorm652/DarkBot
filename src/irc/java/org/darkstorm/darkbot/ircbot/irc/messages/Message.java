package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public abstract class Message {
	MessageType type;
	String raw;

	public Message(MessageType type, String raw) {
		if(type == null)
			throw new NullPointerException();
		this.type = type;
		this.raw = raw;
	}

	public MessageType getType() {
		return type;
	}

	public String getRaw() {
		return raw;
	}
}
