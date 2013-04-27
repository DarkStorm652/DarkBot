package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class UnknownMessage extends Message {
	public UnknownMessage(String raw) {
		super(MessageType.OTHER, raw);
	}
}
