package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.parsing.UserInfo;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class InviteMessage extends Message {
	private String senderNickname;
	private String senderUsername;
	private String senderHostname;
	private String receiver;
	private Channel channel;

	public InviteMessage(String raw, UserInfo sender, String receiver,
			Channel channel) {
		super(MessageType.INVITE, raw);
		senderNickname = sender.getNickname();
		senderUsername = sender.getUsername();
		senderHostname = sender.getHostname();
		this.receiver = receiver;
		this.channel = channel;
	}

	public String getSenderNickname() {
		return senderNickname;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public String getSenderHostname() {
		return senderHostname;
	}

	public String getReceiver() {
		return receiver;
	}

	public Channel getChannel() {
		return channel;
	}
}
