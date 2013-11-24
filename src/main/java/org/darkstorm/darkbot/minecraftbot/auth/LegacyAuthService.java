package org.darkstorm.darkbot.minecraftbot.auth;

import java.io.*;
import java.net.*;

import org.darkstorm.darkbot.minecraftbot.util.Util;

public class LegacyAuthService implements AuthService {
	private static final String LOGIN_URL = "https://login.minecraft.net/";
	private static final String LOGIN_URL_PARAMETERS = "user=%s&password=%s&version=%d";
	private static final int LOGIN_VERSION = 12;
	private static final String SERVER_AUTH_URL = "http://session.minecraft.net/game/joinserver.jsp?user=%s&sessionId=%s&serverId=%s";

	@Override
	public LegacySession login(String username, String password) throws AuthenticationException, IOException {
		return login(username, password, null);
	}

	@Override
	public LegacySession login(String username, String password, Proxy proxy) throws AuthenticationException, IOException {
		String parameters = String.format(LOGIN_URL_PARAMETERS, encodeUtf8(username), encodeUtf8(password), LOGIN_VERSION);
		String result = Util.post(LOGIN_URL, parameters, proxy);
		if(result == null)
			throw new IOException("Null result?");
		if(!result.contains(":"))
			throw new AuthenticationException(result.trim());
		String[] values = result.split(":");
		return new LegacySession(values[2], password, values[3].replaceAll("[\n\r]", ""));
	}

	@Override
	public void authenticate(Session session, String serverId) throws AuthenticationException, IOException {
		authenticate(session, serverId, null);
	}

	@Override
	public void authenticate(Session session, String serverId, Proxy proxy) throws AuthenticationException, IOException {
		if(!(session instanceof LegacySession))
			throw new IllegalArgumentException();
		LegacySession legacySession = (LegacySession) session;

		URL url = new URL(String.format(SERVER_AUTH_URL, encodeUtf8(legacySession.getUsername()), encodeUtf8(legacySession.getSessionId()), encodeUtf8(serverId)));
		URLConnection connection = proxy == null ? url.openConnection() : url.openConnection(proxy);
		connection.setConnectTimeout(30000);
		connection.setReadTimeout(30000);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String response = reader.readLine();
			if(!response.equalsIgnoreCase("ok"))
				throw new AuthenticationException(response);
		}
	}

	private String encodeUtf8(String string) throws IOException {
		return URLEncoder.encode(string, "UTF-8");
	}

	@Override
	public boolean isValidSession(Session session) {
		return session instanceof LegacySession;
	}
}
