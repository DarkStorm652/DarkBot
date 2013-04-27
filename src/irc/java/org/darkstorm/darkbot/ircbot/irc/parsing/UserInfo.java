package org.darkstorm.darkbot.ircbot.irc.parsing;

public final class UserInfo {
	private String nickname;
	private String username;
	private String hostname;

	public UserInfo(String nickname, String username, String hostname) {
		this.nickname = nickname;
		this.username = username;
		this.hostname = hostname;
	}

	public String getNickname() {
		return nickname;
	}

	public String getUsername() {
		return username;
	}

	public String getHostname() {
		return hostname;
	}

	@Override
	public String toString() {
		return nickname + "!" + username + "@" + hostname;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			throw new NullPointerException();
		return obj instanceof UserInfo && obj.toString().equals(toString());
	}
}
