package org.darkstorm.minecraft.darkbot.auth;

public class OfflineSession extends Session {
	public OfflineSession(String username) {
		super(username, null);
	}

	@Override
	public boolean isValidForLogin() {
		return false;
	}

	@Override
	public boolean isValidForAuthentication() {
		return false;
	}

	@Override
	public String toString() {
		return "OfflineSession{username=" + getUsername() + "}";
	}
}
