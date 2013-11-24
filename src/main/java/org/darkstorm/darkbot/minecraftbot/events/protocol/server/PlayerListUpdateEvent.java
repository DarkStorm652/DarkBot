package org.darkstorm.darkbot.minecraftbot.events.protocol.server;


public class PlayerListUpdateEvent extends PlayerListEvent {
	private final int ping;

	public PlayerListUpdateEvent(String playerName, int ping) {
		super(playerName);
		this.ping = ping;
	}

	public int getPing() {
		return ping;
	}
}
