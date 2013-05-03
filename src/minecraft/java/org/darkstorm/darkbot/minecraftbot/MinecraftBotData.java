package org.darkstorm.darkbot.minecraftbot;

import java.util.*;

import joptsimple.*;

import org.darkstorm.darkbot.bot.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData;

public class MinecraftBotData {
	private final String server;
	private final int port;
	private final int protocol;

	private final String username;
	private final String password;
	private final String sessionId;

	private final ProxyData httpProxy;
	private final ProxyData socksProxy;

	public MinecraftBotData(String server, int port, int protocol,
			String username, String password, String sessionId,
			ProxyData httpProxy, ProxyData socksProxy) {
		this.server = server;
		this.port = port;
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
		this.httpProxy = httpProxy;
		this.socksProxy = socksProxy;
	}

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

	public int getProtocol() {
		return protocol;
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

	public ProxyData getHttpProxy() {
		return httpProxy;
	}

	public ProxyData getSocksProxy() {
		return socksProxy;
	}

	public static final Builder builder() {
		return new Builder();
	}

	public static final class Builder extends BotData {
		private String server;
		private int port = MinecraftBot.DEFAULT_PORT;
		private int protocol = MinecraftBot.PROTOCOL_VERSION;

		private String username;
		private String password;
		private String sessionId;

		private ProxyData httpProxy;
		private ProxyData socksProxy;

		private Builder() {
		}

		public String getServer() {
			return server;
		}

		public Builder withServer(String server) {
			this.server = server;
			return this;
		}

		public int getPort() {
			return port;
		}

		public Builder withPort(int port) {
			this.port = port;
			return this;
		}

		public int getProtocol() {
			return protocol;
		}

		public Builder withProtocol(int protocol) {
			this.protocol = protocol;
			return this;
		}

		public String getUsername() {
			return username;
		}

		public Builder withUsername(String username) {
			this.username = username;
			return this;
		}

		public String getPassword() {
			return password;
		}

		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}

		public String getSessionId() {
			return sessionId;
		}

		public Builder withSessionId(String sessionId) {
			this.sessionId = sessionId;
			return this;
		}

		public ProxyData getHttpProxy() {
			return httpProxy;
		}

		public Builder withHttpProxy(ProxyData httpProxy) {
			this.httpProxy = httpProxy;
			return this;
		}

		public ProxyData getSocksProxy() {
			return socksProxy;
		}

		public Builder withSocksProxy(ProxyData socksProxy) {
			this.socksProxy = socksProxy;
			return this;
		}

		public MinecraftBotData build() {
			assert isValid();
			return new MinecraftBotData(server, port, protocol, username,
					password, sessionId, httpProxy, socksProxy);
		}

		@Override
		public void parse(OptionSet options) {
			server = (String) options.valueOf("server");
			username = (String) options.valueOf("username");
			if(options.has("port"))
				port = (Integer) options.valueOf("port");
			if(options.has("password"))
				password = (String) options.valueOf("password");
			if(options.has("session"))
				sessionId = (String) options.valueOf("session");
		}

		@Override
		public boolean isValid() {
			return username != null && !username.isEmpty() && server != null
					&& !server.isEmpty() && port >= 0 && port < 65535;
		}

		@BotArgumentHandler
		public static OptionSpec<?>[] getArguments() {
			OptionParser parser = new OptionParser();
			List<OptionSpec<?>> arguments = new ArrayList<OptionSpec<?>>();
			arguments.add(parser.acceptsAll(Arrays.asList("s", "server"))
					.withRequiredArg().ofType(String.class)
					.describedAs("server"));
			arguments.add(parser.acceptsAll(Arrays.asList("p", "port"))
					.withRequiredArg().ofType(Integer.class)
					.describedAs("port"));
			arguments.add(parser.acceptsAll(Arrays.asList("n", "nickname"))
					.withRequiredArg().ofType(String.class)
					.describedAs("nickname"));
			arguments.add(parser.acceptsAll(Arrays.asList("P", "password"))
					.withRequiredArg().ofType(String.class)
					.describedAs("password"));
			arguments.add(parser.acceptsAll(Arrays.asList("o", "owner"))
					.withRequiredArg().ofType(String.class)
					.describedAs("owner"));
			arguments.add(parser.acceptsAll(Arrays.asList("P", "proxy"))
					.withRequiredArg().ofType(String.class)
					.describedAs("host:port:type"));
			return arguments.toArray(new OptionSpec<?>[arguments.size()]);
		}
	}
}
