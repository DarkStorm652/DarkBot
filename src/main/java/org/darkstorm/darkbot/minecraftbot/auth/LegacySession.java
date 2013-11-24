package org.darkstorm.darkbot.minecraftbot.auth;

import org.apache.commons.lang3.StringUtils;

public class LegacySession extends Session {
	private final String sessionId;

	public LegacySession(String username, String password, String sessionId) {
		super(username, password);
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	public boolean isValidForAuthentication() {
		return StringUtils.isNotBlank(sessionId);
	}

	@Override
	public String toString() {
		return "LegacySession{username=" + getUsername() + ",password=" + getPassword() + ",session=" + sessionId + "}";
	}
}
