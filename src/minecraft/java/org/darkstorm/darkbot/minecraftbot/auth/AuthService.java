package org.darkstorm.darkbot.minecraftbot.auth;

import java.io.IOException;
import java.net.Proxy;

public interface AuthService {
	public Session login(String username, String password) throws AuthenticationException, IOException;

	public Session login(String username, String password, Proxy proxy) throws AuthenticationException, IOException;

	public void authenticate(Session session, String serverId) throws AuthenticationException, IOException;

	public void authenticate(Session session, String serverId, Proxy proxy) throws AuthenticationException, IOException;

	public boolean isValidSession(Session session);
}
