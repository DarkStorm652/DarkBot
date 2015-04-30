package org.darkstorm.minecraft.darkbot.protocol;

/**
 * Represents a protocol from Minecraft versions 1.7 and on, where the protocol
 * was entirely redone.
 */
public interface ProtocolX<H extends PacketHeader> extends Protocol<H> {
	public enum State {
		HANDSHAKE,
		STATUS,
		LOGIN,
		PLAY
	}

	public State getState();
}
