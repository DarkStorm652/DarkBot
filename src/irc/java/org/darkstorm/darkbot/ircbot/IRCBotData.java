package org.darkstorm.darkbot.ircbot;

import java.util.*;

import joptsimple.*;

import org.darkstorm.darkbot.bot.*;
import org.darkstorm.darkbot.ircbot.irc.Channel;

public class IRCBotData extends BotData {
	public String server;
	public int port;

	public String nickname;
	public String password = "";

	public String owner;
	public String[] channels;

	@Override
	public void parse(OptionSet options) {
		if(!options.has("server") || !options.has("nickname")
				|| !options.has("owner"))
			throw new RuntimeException("Missing some arguments");
		server = (String) options.valueOf("server");
		nickname = (String) options.valueOf("nickname");
		owner = (String) options.valueOf("owner");
		port = IRCBot.DEFAULT_PORT;
		if(options.has("port"))
			port = (Integer) options.valueOf("port");
		if(options.has("password"))
			password = (String) options.valueOf("password");
		else
			password = "";
		if(options.has("channels"))
			channels = ((ChannelList) options.valueOf("channels"))
					.getChannels();
	}

	@Override
	public boolean isValid() {
		if(channels != null)
			for(String channel : channels)
				if(channel == null || !Channel.isChannel(channel))
					return false;
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
		arguments.add(parser.acceptsAll(Arrays.asList("c", "channels"))
				.withRequiredArg().describedAs("channels")
				.withValuesConvertedBy(new ChannelValueConverter()));
		return arguments.toArray(new OptionSpec<?>[arguments.size()]);
	}

	private static class ChannelValueConverter implements
			ValueConverter<ChannelList> {
		@Override
		public ChannelList convert(String value) {
			ArrayList<String> converted = new ArrayList<String>();
			String[] channels = value.split(",");
			for(String channel : channels) {
				channel = channel.trim();
				if(!Channel.isChannel(channel)) {
					System.err.println("Skipping channel: " + channel);
					continue;
				}
				converted.add(channel);
			}
			return new ChannelList(converted.toArray(new String[converted
					.size()]));
		}

		@Override
		public String valuePattern() {
			return null;
		}

		@Override
		public Class<ChannelList> valueType() {
			return ChannelList.class;
		}
	}
}

class ChannelList {
	private String[] channels;

	public ChannelList(String[] channels) {
		this.channels = channels;
	}

	public String[] getChannels() {
		return channels;
	}
}