package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.parsing.UserInfo;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class NickMessage extends Message {
	private String originalNickname;
	private String newNickname;
	private String userUsername;
	private String userHostname;

	public NickMessage(String raw, UserInfo user, String newNickname) {
		super(MessageType.NICK, raw);
		originalNickname = user.getNickname();
		this.newNickname = newNickname;
		userUsername = user.getUsername();
		userHostname = user.getHostname();
	}

	public String getOriginalNickname() {
		return originalNickname;
	}

	public String getNewNickname() {
		return newNickname;
	}

	public String getUserUsername() {
		return userUsername;
	}

	public String getUserHostname() {
		return userHostname;
	}
}
