package org.darkstorm.darkbot.minecraftbot.auth;

import org.apache.commons.lang3.StringUtils;

public abstract class Session {
	private final String username, password;

	public Session(String username, String password) {
		if(username == null)
			throw new NullPointerException("Null username");
		if(StringUtils.isBlank(username))
			throw new IllegalArgumentException("Empty username");
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isValidForLogin() {
		return StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
	}

	public abstract boolean isValidForAuthentication();

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{username=" + username + ",password=" + password + "}";
	}
}
