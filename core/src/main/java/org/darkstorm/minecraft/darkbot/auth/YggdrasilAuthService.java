package org.darkstorm.minecraft.darkbot.auth;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

import org.darkstorm.minecraft.darkbot.auth.YggdrasilSession.Profile;
import org.darkstorm.minecraft.darkbot.util.ProxyData;
import org.json.simple.*;
import org.json.simple.parser.*;

public class YggdrasilAuthService implements AuthService<YggdrasilSession> {
	private static final String LOGIN_URL = "https://authserver.mojang.com";
	private static final String LOGIN_AUTHENTICATE_URL = LOGIN_URL + "/authenticate";
	private static final String LOGIN_REFRESH_URL = LOGIN_URL + "/refresh";
	private static final String LOGIN_VALIDATE_URL = LOGIN_URL + "/validate";
	private static final String LOGIN_SIGNOUT_URL = LOGIN_URL + "/signout";
	private static final String LOGIN_INVALIDATE_URL = LOGIN_URL + "/invalidate";
	private static final String SERVER_AUTHENTICATE_URL = "https://sessionserver.mojang.com/session/minecraft/join";
	private static final int LOGIN_VERSION = 1;

	private final UUID clientToken;

	public YggdrasilAuthService(UUID clientToken) {
		this.clientToken = clientToken;
	}

	@Override
	public YggdrasilSession login(String username, String password) throws AuthenticationException, IOException {
		return login(username, password, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public YggdrasilSession login(String username, String password, ProxyData proxy) throws AuthenticationException, IOException {
		JSONObject agent = new JSONObject();
		agent.put("name", "Minecraft");
		agent.put("version", LOGIN_VERSION);

		JSONObject request = new JSONObject();
		request.put("agent", agent);
		request.put("username", username);
		request.put("password", password);
		request.put("clientToken", clientToken.toString());

		JSONObject response = post(LOGIN_AUTHENTICATE_URL, request, proxy);
		if(response == null)
			throw new IOException("Empty login response");
		checkError(response);

		try {
			return parse(response, password);
		} catch(Exception exception) {
			throw new IOException("Unable to parse server response", exception);
		}
	}

	public void logout(YggdrasilSession session) throws AuthenticationException, IOException {
		logout(session, null);
	}

	public void logout(YggdrasilSession session, ProxyData proxy) throws AuthenticationException, IOException {
		logout(session.getUsername(), session.getPassword(), proxy);
	}

	public void logout(String username, String password) throws AuthenticationException, IOException {
		logout(username, password, null);
	}

	@SuppressWarnings("unchecked")
	public void logout(String username, String password, ProxyData proxy) throws AuthenticationException, IOException {
		if(username == null || username.trim().isEmpty())
			throw new IllegalArgumentException("Invalid username");
		if(password == null || password.trim().isEmpty())
			throw new IllegalArgumentException("Invalid password");

		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("password", password);

		JSONObject response = post(LOGIN_SIGNOUT_URL, request, proxy);
		if(response != null)
			checkError(response);
	}

	public void validate(YggdrasilSession session) throws AuthenticationException, IOException {
		validate(session, null);
	}

	@SuppressWarnings("unchecked")
	public void validate(YggdrasilSession session, ProxyData proxy) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to validate");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));

		JSONObject response = post(LOGIN_VALIDATE_URL, request, proxy);
		if(response != null)
			checkError(response);
	}

	public void invalidate(YggdrasilSession session) throws AuthenticationException, IOException {
		validate(session, null);
	}

	@SuppressWarnings("unchecked")
	public void invalidate(YggdrasilSession session, ProxyData proxy) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to invalidate");
		if(session.getClientToken() == null)
			throw new IllegalArgumentException("Session must have client token to refresh");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));
		request.put("clientToken", session.getClientToken().toString());

		JSONObject response = post(LOGIN_INVALIDATE_URL, request, proxy);
		if(response != null)
			checkError(response);
	}

	public YggdrasilSession refresh(YggdrasilSession session) throws AuthenticationException, IOException {
		return refresh(session, null);
	}

	@SuppressWarnings("unchecked")
	public YggdrasilSession refresh(YggdrasilSession session, ProxyData proxy) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to refresh");
		if(session.getClientToken() == null)
			throw new IllegalArgumentException("Session must have client token to refresh");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));
		request.put("clientToken", session.getClientToken().toString());

		JSONObject response = post(LOGIN_REFRESH_URL, request, proxy);
		try {
			YggdrasilSession newSession = parse(response, session.getPassword());
			return new YggdrasilSession(newSession.getUsername(), newSession.getPassword(), newSession.getAccessToken(), newSession.getClientToken(), newSession.getSelectedProfile(), session.getAvailableProfiles());
		} catch(Exception exception) {
			throw new IOException("Unable to parse server response", exception);
		}
	}

	@Override
	public void authenticate(YggdrasilSession session, String serverId) throws AuthenticationException, IOException {
		authenticate(session, serverId, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void authenticate(YggdrasilSession session, String serverId, ProxyData proxy) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to refresh");
		if(session.getSelectedProfile() == null)
			throw new IllegalArgumentException("Session must have selected profile");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));
		request.put("selectedProfile", session.getSelectedProfile().getId());
		request.put("serverId", serverId);

		JSONObject response = post(SERVER_AUTHENTICATE_URL, request, proxy);
		if(response != null)
			checkError(response);
	}

	private JSONObject post(String targetURL, JSONObject request, ProxyData proxy) throws IOException {
		Proxy wrappedProxy = wrapProxy(proxy);
		String requestValue = request.toJSONString();
		HttpsURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			if(wrappedProxy != null)
				connection = (HttpsURLConnection) url.openConnection(wrappedProxy);
			else
				connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			connection.setRequestProperty("Content-Length", Integer.toString(requestValue.length()));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(3000);

			connection.connect();

			/*Certificate[] certs = connection.getServerCertificates();

			byte[] bytes = new byte[294];
			DataInputStream dis = new DataInputStream(Session.class.getResourceAsStream("/minecraft.key"));
			dis.readFully(bytes);
			dis.close();

			Certificate c = certs[0];
			PublicKey pk = c.getPublicKey();
			byte[] data = pk.getEncoded();

			for(int i = 0; i < data.length; i++)
				if(data[i] != bytes[i])
					throw new RuntimeException("Public key mismatch");*/

			try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
				out.writeBytes(requestValue);
				out.flush();
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				StringBuffer response = new StringBuffer();
				String line;
				while((line = reader.readLine()) != null) {
					if(response.length() > 0)
						response.append('\n');
					response.append(line);
				}
				if(response.toString().trim().isEmpty())
					return null;
				try {
					JSONParser parser = new JSONParser();
					Object responseObject = parser.parse(response.toString());
					if(!(responseObject instanceof JSONObject))
						throw new IOException("Response not type of JSONObject: " + response);
					return (JSONObject) responseObject;
				} catch(ParseException exception) {
					throw new IOException("Response not valid JSON: " + response, exception);
				}
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

	private Proxy wrapProxy(ProxyData proxy) {
		if(proxy == null || (proxy.getType() != ProxyData.ProxyType.HTTP && proxy.getType() != ProxyData.ProxyType.SOCKS))
			return null;
		return new Proxy(proxy.getType() == ProxyData.ProxyType.HTTP ? Proxy.Type.HTTP : Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHostName(), proxy.getPort()));
	}

	private void checkError(JSONObject response) throws AuthenticationException {
		if(response.containsKey("error")) {
			String error = (String) response.get("error");
			String errorMessage = (String) response.get("errorMessage");
			if(response.containsKey("cause")) {
				String errorCause = (String) response.get("cause");
				throw new YggdrasilAuthenticationException(error, errorMessage, errorCause);
			} else
				throw new YggdrasilAuthenticationException(error, errorMessage);
		}
	}

	private YggdrasilSession parse(JSONObject response, String password) {
		String accessTokenValue = (String) response.get("accessToken");
		String clientTokenValue = (String) response.get("clientToken");
		JSONArray availableProfilesValue = (JSONArray) response.get("availableProfiles");
		JSONObject selectedProfileValue = (JSONObject) response.get("selectedProfile");

		BigInteger accessToken = new BigInteger(accessTokenValue, 16);
		UUID clientToken = clientTokenValue != null ? UUID.fromString(clientTokenValue) : null;
		List<Profile> availableProfiles = new ArrayList<>();
		if(availableProfilesValue != null) {
			for(Object object : availableProfilesValue) {
				if(!(object instanceof JSONObject))
					continue;
				JSONObject profileJson = (JSONObject) object;
				String id = (String) profileJson.get("id");
				String name = (String) profileJson.get("name");
				if(id != null && name != null)
					availableProfiles.add(new Profile(id, name));
			}
		}
		String profileId = (String) selectedProfileValue.get("id");
		String profileName = (String) selectedProfileValue.get("name");
		if(profileId == null || profileName == null)
			throw new NullPointerException("No selected profile id or name passed");
		Profile selectedProfile = new Profile(profileId, profileName);

		return new YggdrasilSession(selectedProfile.getName(), password, accessToken, clientToken, selectedProfile, availableProfiles.toArray(new Profile[availableProfiles.size()]));
	}

	@Override
	public YggdrasilSession validateSession(Session session) throws InvalidSessionException {
		if(!(session instanceof YggdrasilSession))
			throw new InvalidSessionException("Wrong type of session");
		if(session.getUsername() == null || session.getUsername().trim().isEmpty())
			throw new InvalidSessionException("Invalid session username");
		return (YggdrasilSession) session;
	}
}
