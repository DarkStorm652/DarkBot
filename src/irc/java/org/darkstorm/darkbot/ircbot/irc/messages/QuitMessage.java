package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.parsing.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class QuitMessage extends Message {
	private final UserInfo user;
	private String message;

	public QuitMessage(String raw, UserInfo user, String message) {
		super(MessageType.QUIT, raw);
		this.user = user;
		this.message = message;
	}

	public UserInfo getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}
}
