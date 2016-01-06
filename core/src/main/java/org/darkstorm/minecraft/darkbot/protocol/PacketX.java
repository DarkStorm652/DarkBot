package org.darkstorm.minecraft.darkbot.protocol;

import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

public interface PacketX extends Packet {
	public enum Direction {
		UPSTREAM,
		DOWNSTREAM
	}

	public State getState();

	public Direction getDirection();
}
