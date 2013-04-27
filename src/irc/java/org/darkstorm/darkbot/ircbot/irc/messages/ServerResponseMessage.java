package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.constants.ServerResponseCodes;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class ServerResponseMessage extends Message {
	private String serverName;
	private int responseCode;
	private String target;
	private String[] extraInfo;

	public ServerResponseMessage(String raw, String serverName,
			int responseCode, String target, String[] extraInfo) {
		super(MessageType.SERVER, raw);
		this.serverName = serverName;
		this.responseCode = responseCode;
		this.target = target;
		this.extraInfo = extraInfo;
	}

	public String getServerName() {
		return serverName;
	}

	/**
	 * @see ServerResponseCodes
	 */
	public int getResponseCode() {
		return responseCode;
	}

	public String getTarget() {
		return target;
	}

	/**
	 * All things before the second colon and after the target name in the raw
	 * message separated by a space, followed by the message after the second
	 * colon.<br/>
	 * <br/>
	 * For example, server response 254 RPL_LUSERCHANNELS may look like
	 * <code>"2421", "channels formed"</code> for the raw message
	 * <code>":irc.example.net 254 ExampleUser 2421 :channels formed"</code>
	 */
	public String[] getExtraInfo() {
		return extraInfo;
	}
}
