package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.parsing.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class KickMessage extends Message {
	private final UserInfo sender;
	private String targetNickname;
	private Channel channel;
	private String message;

	public KickMessage(String raw, UserInfo sender, Channel channel,
			String targetNickname, String message) {
		super(MessageType.KICK, raw);
		this.sender = sender;
		this.targetNickname = targetNickname;
		this.channel = channel;
		this.message = message;
	}

	public UserInfo getSender() {
		return sender;
	}

	public String getTargetNickname() {
		return targetNickname;
	}

	public Channel getChannel() {
		return channel;
	}

	public String getMessage() {
		return message;
	}
}
