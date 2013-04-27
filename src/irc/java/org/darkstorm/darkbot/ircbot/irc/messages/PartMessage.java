package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.parsing.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class PartMessage extends Message {
	private final UserInfo user;
	private Channel channel;
	private String message;

	public PartMessage(String raw, UserInfo user, Channel channel,
			String message) {
		super(MessageType.PART, raw);
		this.user = user;
		this.channel = channel;
		this.message = message;
	}

	public UserInfo getUser() {
		return user;
	}

	public Channel getChannel() {
		return channel;
	}

	public String getMessage() {
		return message;
	}
}
