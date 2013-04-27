package org.darkstorm.darkbot.minecraftbot;

public final class Session {
	private final String username, password, sessionId;

	public Session(String username, String password, String sessionId) {
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSessionId() {
		return sessionId;
	}
}
