package org.darkstorm.darkbot.minecraftbot;

import java.util.*;

import joptsimple.*;

import org.darkstorm.darkbot.bot.*;
import org.darkstorm.darkbot.minecraftbot.auth.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData;

public final class MinecraftBotData {
	private final String server;
	private final int port;
	private final int protocol;

	private final String username;
	private final String password;

	private final ProxyData httpProxy;
	private final ProxyData socksProxy;

	private final AuthService authService;
	private final Session session;

	private MinecraftBotData(Builder builder) {
		server = builder.server;
		port = builder.port;
		protocol = builder.protocol;
		username = builder.username;
		password = builder.password;
		httpProxy = builder.httpProxy;
		socksProxy = builder.socksProxy;
		authService = builder.authService;
		session = builder.session;
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

	public ProxyData getHttpProxy() {
		return httpProxy;
	}

	public ProxyData getSocksProxy() {
		return socksProxy;
	}

	public AuthService getAuthService() {
		return authService;
	}

	public Session getSession() {
		return session;
	}

	public static final Builder builder() {
		return new Builder();
	}

	public static final class Builder extends BotData {
		private String server;
		private int port = MinecraftBot.DEFAULT_PORT;
		private int protocol = MinecraftBot.LATEST_PROTOCOL;

		private String username;
		private String password;

		private ProxyData httpProxy;
		private ProxyData socksProxy;

		private AuthService authService;
		private Session session;

		private Builder() {
		}

		public Builder server(String server) {
			this.server = server;
			return this;
		}

		public Builder port(int port) {
			this.port = port;
			return this;
		}

		public Builder protocol(int protocol) {
			this.protocol = protocol;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder httpProxy(ProxyData httpProxy) {
			this.httpProxy = httpProxy;
			return this;
		}

		public Builder socksProxy(ProxyData socksProxy) {
			this.socksProxy = socksProxy;
			return this;
		}

		public Builder authService(AuthService authService) {
			this.authService = authService;
			return this;
		}

		public Builder session(Session session) {
			this.session = session;
			return this;
		}

		public MinecraftBotData build() {
			assert isValid();
			return new MinecraftBotData(this);
		}

		@Override
		public void parse(OptionSet options) {
			server = (String) options.valueOf("server");
			username = (String) options.valueOf("username");
			if(options.has("port"))
				port = (Integer) options.valueOf("port");
			if(options.has("password"))
				password = (String) options.valueOf("password");
		}

		@Override
		public boolean isValid() {
			return username != null && !username.isEmpty() && server != null && !server.isEmpty() && port >= 0 && port < 65535;
		}

		@BotArgumentHandler
		public static OptionSpec<?>[] getArguments() {
			OptionParser parser = new OptionParser();
			List<OptionSpec<?>> arguments = new ArrayList<OptionSpec<?>>();
			arguments.add(parser.acceptsAll(Arrays.asList("s", "server")).withRequiredArg().ofType(String.class).describedAs("server"));
			arguments.add(parser.acceptsAll(Arrays.asList("p", "port")).withRequiredArg().ofType(Integer.class).describedAs("port"));
			arguments.add(parser.acceptsAll(Arrays.asList("n", "nickname")).withRequiredArg().ofType(String.class).describedAs("nickname"));
			arguments.add(parser.acceptsAll(Arrays.asList("P", "password")).withRequiredArg().ofType(String.class).describedAs("password"));
			arguments.add(parser.acceptsAll(Arrays.asList("o", "owner")).withRequiredArg().ofType(String.class).describedAs("owner"));
			arguments.add(parser.acceptsAll(Arrays.asList("P", "proxy")).withRequiredArg().ofType(String.class).describedAs("host:port:type"));
			return arguments.toArray(new OptionSpec<?>[arguments.size()]);
		}
	}
}
