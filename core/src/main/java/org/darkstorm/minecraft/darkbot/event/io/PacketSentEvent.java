package org.darkstorm.darkbot.minecraftbot.event.io;

import org.darkstorm.darkbot.minecraftbot.protocol.WriteablePacket;

public class PacketSentEvent extends PacketEvent {
	private final WriteablePacket packet;

	public PacketSentEvent(WriteablePacket packet) {
		this.packet = packet;
	}

	@Override
	public WriteablePacket getPacket() {
		return packet;
	}
}
