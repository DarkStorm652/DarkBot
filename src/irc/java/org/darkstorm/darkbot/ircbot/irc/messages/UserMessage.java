package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.parsing.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class UserMessage extends Message {
	private final UserInfo sender;
	private String receiverNickname;
	private String message;
	private boolean ctcp;

	public UserMessage(MessageType type, String raw, UserInfo sender,
			String receiverNickname, String message, boolean ctcp) {
		super(type, raw);
		if(!type.equals(MessageType.MESSAGE)
				&& !type.equals(MessageType.NOTICE))
			throw new IllegalArgumentException("type is not MESSAGE or NOTICE");
		this.sender = sender;
		this.receiverNickname = receiverNickname;
		this.message = message;
		this.ctcp = ctcp;

	}

	public UserInfo getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiverNickname;
	}

	public String getMessage() {
		return message;
	}

	public boolean isCTCP() {
		return ctcp;
	}
}
