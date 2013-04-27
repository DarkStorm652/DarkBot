package org.darkstorm.darkbot.minecraftbot;

import java.util.*;

import joptsimple.*;

import org.darkstorm.darkbot.bot.*;
import org.darkstorm.darkbot.minecraftbot.util.ProxyData;

public class MinecraftBotData extends BotData {
	public String server;
	public int port = MinecraftBot.DEFAULT_PORT;

	public ProxyData proxy;

	public String nickname;
	public String password = "";
	public String sessionId;

	public String loginProxy;
	public int loginProxyPort = 80;

	public boolean authenticate = true;

	public String owner;

	@Override
	public void parse(OptionSet options) {
		if(!options.has("server") || !options.has("nickname")
				|| !options.has("owner"))
			throw new RuntimeException("Missing some arguments");
		server = (String) options.valueOf("server");
		nickname = (String) options.valueOf("nickname");
		owner = (String) options.valueOf("owner");
		port = MinecraftBot.DEFAULT_PORT;
		if(options.has("port"))
			port = (Integer) options.valueOf("port");
		if(options.has("password"))
			password = (String) options.valueOf("password");
		else
			password = "";
		authenticate = !password.isEmpty();
	}

	@Override
	public boolean isValid() {
		return nickname != null && password != null && server != null
				&& port >= 0 && owner != null;
	}

	@BotArgumentHandler
	public static OptionSpec<?>[] getArguments() {
		OptionParser parser = new OptionParser();
		ArrayList<OptionSpec<?>> arguments = new ArrayList<OptionSpec<?>>();
		arguments.add(parser.acceptsAll(Arrays.asList("s", "server"))
				.withRequiredArg().ofType(String.class).describedAs("server"));
		arguments.add(parser.acceptsAll(Arrays.asList("p", "port"))
				.withRequiredArg().ofType(Integer.class).describedAs("port"));
		arguments
				.add(parser.acceptsAll(Arrays.asList("n", "nickname"))
						.withRequiredArg().ofType(String.class)
						.describedAs("nickname"));
		arguments
				.add(parser.acceptsAll(Arrays.asList("P", "password"))
						.withRequiredArg().ofType(String.class)
						.describedAs("password"));
		arguments.add(parser.acceptsAll(Arrays.asList("o", "owner"))
				.withRequiredArg().ofType(String.class).describedAs("owner"));
		arguments.add(parser.acceptsAll(Arrays.asList("P", "proxy"))
				.withRequiredArg().ofType(String.class)
				.describedAs("host:port:type"));
		return arguments.toArray(new OptionSpec<?>[arguments.size()]);
	}
}