package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.parsing.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class JoinMessage extends Message {
	private final UserInfo user;
	private final Channel channel;

	public JoinMessage(String raw, UserInfo user, Channel channel) {
		super(MessageType.JOIN, raw);
		this.user = user;
		this.channel = channel;
	}

	public UserInfo getUser() {
		return user;
	}

	public Channel getChannel() {
		return channel;
	}
}
