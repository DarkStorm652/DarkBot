package org.darkstorm.minecraft.darkbot.auth;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;

import org.darkstorm.minecraft.darkbot.util.ProxyData;

public class LegacyAuthService implements AuthService<LegacySession> {
	private static final String LOGIN_URL = "https://login.minecraft.net/";
	private static final String LOGIN_URL_PARAMETERS = "user=%s&password=%s&version=%d";
	private static final int LOGIN_VERSION = 12;
	private static final String SERVER_AUTH_URL = "http://session.minecraft.net/game/joinserver.jsp?user=%s&sessionId=%s&serverId=%s";

	@Override
	public LegacySession login(String username, String password) throws AuthenticationException, IOException {
		return login(username, password, null);
	}

	@Override
	public LegacySession login(String username, String password, ProxyData proxy) throws AuthenticationException, IOException {
		String parameters = String.format(LOGIN_URL_PARAMETERS, encodeUtf8(username), encodeUtf8(password), LOGIN_VERSION);
		String result = post(LOGIN_URL, parameters, proxy);
		if(result == null)
			throw new IOException("Null result?");
		if(!result.contains(":"))
			throw new AuthenticationException(result.trim());
		String[] values = result.split(":");
		return new LegacySession(values[2], password, values[3].replaceAll("[\n\r]", ""));
	}

	private String post(String targetURL, String urlParameters, ProxyData proxy) throws IOException {
		Proxy wrappedProxy = wrapProxy(proxy);
		HttpsURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			if(wrappedProxy != null)
				connection = (HttpsURLConnection) url.openConnection(wrappedProxy);
			else
				connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(3000);

			connection.connect();

			Certificate[] certs = connection.getServerCertificates();

			byte[] bytes = new byte[294];
			DataInputStream dis = new DataInputStream(Session.class.getResourceAsStream("/minecraft.key"));
			dis.readFully(bytes);
			dis.close();

			Certificate c = certs[0];
			PublicKey pk = c.getPublicKey();
			byte[] data = pk.getEncoded();

			for(int i = 0; i < data.length; i++)
				if(data[i] != bytes[i])
					throw new RuntimeException("Public key mismatch");

			try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
				out.writeBytes(urlParameters);
				out.flush();
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				StringBuffer response = new StringBuffer();
				String line;
				while((line = reader.readLine()) != null)
					response.append(line).append('\r');
				return response.toString();
			}
		} catch(IOException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new IOException("Error connecting", exception);
		} finally {
			if(connection != null)
				connection.disconnect();
		}
	}

	@Override
	public void authenticate(LegacySession session, String serverId) throws AuthenticationException, IOException {
		authenticate(session, serverId, null);
	}

	@Override
	public void authenticate(LegacySession session, String serverId, ProxyData proxy) throws AuthenticationException, IOException {
		Proxy wrappedProxy = wrapProxy(proxy);
		URL url = new URL(String.format(SERVER_AUTH_URL, encodeUtf8(session.getUsername()), encodeUtf8(session.getSessionId()), encodeUtf8(serverId)));
		URLConnection connection = wrappedProxy == null ? url.openConnection() : url.openConnection(wrappedProxy);
		connection.setConnectTimeout(30000);
		connection.setReadTimeout(30000);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String response = reader.readLine();
			if(!response.equalsIgnoreCase("ok"))
				throw new AuthenticationException(response);
		}
	}

	private Proxy wrapProxy(ProxyData proxy) {
		if(proxy == null || (proxy.getType() != ProxyData.ProxyType.HTTP && proxy.getType() != ProxyData.ProxyType.SOCKS))
			return null;
		return new Proxy(proxy.getType() == ProxyData.ProxyType.HTTP ? Proxy.Type.HTTP : Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHostName(), proxy.getPort()));
	}

	private String encodeUtf8(String string) throws IOException {
		return URLEncoder.encode(string, "UTF-8");
	}

	@Override
	public LegacySession validateSession(Session session) throws InvalidSessionException {
		if(!(session instanceof LegacySession))
			throw new InvalidSessionException("Wrong type of session");
		if(session.getUsername() == null || session.getUsername().trim().isEmpty())
			throw new InvalidSessionException("Invalid session username");
		return (LegacySession) session;
	}
}
