package org.darkstorm.darkbot.minecraftbot.protocol;

import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public interface PacketX extends Packet {
	public enum Direction {
		UPSTREAM,
		DOWNSTREAM
	}

	public State getState();

	public Direction getDirection();
}
