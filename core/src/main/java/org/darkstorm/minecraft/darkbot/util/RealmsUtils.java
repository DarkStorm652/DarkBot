package org.darkstorm.minecraft.darkbot.util;

import java.io.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;

import org.darkstorm.minecraft.darkbot.auth.YggdrasilSession;
import org.darkstorm.minecraft.darkbot.connection.ProxyData;
import org.json.simple.*;
import org.json.simple.parser.*;

public final class RealmsUtils {
	private static final String REALMS_URL = "https://mcoapi.minecraft.net/";
	private static final String REALMS_WORLD_URL = REALMS_URL + "worlds";
	private static final String REALMS_WORLD_JOIN_URL = REALMS_URL + "worlds/%s/join";

	private RealmsUtils() {
		throw new UnsupportedOperationException();
	}

	public static Server[] requestServers(YggdrasilSession session) throws IOException {
		return requestServers(session, null);
	}

	public static Server[] requestServers(YggdrasilSession session, ProxyData proxy) throws IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be valid for authentication");

		String response = mcoapiGet(REALMS_WORLD_URL, session, proxy);
		JSONObject json;
		try {
			json = (JSONObject) new JSONParser().parse(response);
		} catch(ParseException exception) {
			throw new IOException("Non-JSON response: " + response, exception);
		}
		JSONArray serverJson = (JSONArray) json.get("servers");
		Server[] servers = new Server[serverJson.size()];
		for(int i = 0; i < servers.length; i++)
			servers[i] = new Server((JSONObject) serverJson.get(i));
		return servers;
	}

	public static String requestAddress(Server server, YggdrasilSession session) throws IOException {
		return requestAddress(server, session, null);
	}

	public static String requestAddress(Server server, YggdrasilSession session, ProxyData proxy) throws IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be valid for authentication");

		String response = mcoapiGet(String.format(REALMS_WORLD_JOIN_URL, server.getId()), session, proxy);
		JSONObject json;
		try {
			json = (JSONObject) new JSONParser().parse(response);
		} catch(ParseException exception) {
			throw new IOException("Non-JSON response: " + response, exception);
		}
		return (String) json.get("address");
	}

	private static String mcoapiGet(String address, YggdrasilSession session, ProxyData proxy) throws IOException {
		Proxy wrappedProxy = wrapProxy(proxy);
		HttpsURLConnection connection;
		if(wrappedProxy != null)
			connection = (HttpsURLConnection) new URL(address).openConnection(wrappedProxy);
		else
			connection = (HttpsURLConnection) new URL(address).openConnection();

		connection.addRequestProperty("Cookie", "user=" + session.getUsername() + ";version=1.7.2;sid=token:" + session.getAccessToken().toString(16) + ":" + session.getSelectedProfile().getId());
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		connection.connect();

		InputStream in = connection.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1)
			out.write(buffer, 0, read);

		return new String(out.toByteArray());
	}

	private static Proxy wrapProxy(ProxyData proxy) {
		if(proxy == null || (proxy.getType() != ProxyData.ProxyType.HTTP && proxy.getType() != ProxyData.ProxyType.SOCKS))
			return null;
		return new Proxy(proxy.getType() == ProxyData.ProxyType.HTTP ? Proxy.Type.HTTP : Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHostName(), proxy.getPort()));
	}

	public static final class Server {
		private final String owner, name, motd, address, state, invited;
		private final int id, difficulty, gameMode, daysLeft;
		private final boolean expired;

		private Server(JSONObject data) {
			id = ((Number) data.get("id")).intValue();
			owner = (String) data.get("owner");
			name = (String) data.get("name");
			motd = (String) data.get("motd");
			address = (String) data.get("ip");
			state = (String) data.get("state");
			difficulty = ((Number) data.get("difficulty")).intValue();
			gameMode = ((Number) data.get("gameMode")).intValue();
			daysLeft = ((Number) data.get("daysLeft")).intValue();
			expired = (Boolean) data.get("expired");
			invited = (String) data.get("invited");
		}

		public int getId() {
			return id;
		}

		public String getOwner() {
			return owner;
		}

		public String getName() {
			return name;
		}

		public String getMotd() {
			return motd;
		}

		public String getAddress() {
			return address;
		}

		public String getState() {
			return state;
		}

		public int getDifficulty() {
			return difficulty;
		}

		public int getGameMode() {
			return gameMode;
		}

		public int getDaysLeft() {
			return daysLeft;
		}

		public boolean isExpired() {
			return expired;
		}

		public String getInvited() {
			return invited;
		}
	}
}
