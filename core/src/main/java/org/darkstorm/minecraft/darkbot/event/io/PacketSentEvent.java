package org.darkstorm.minecraft.darkbot.event.io;

import org.darkstorm.minecraft.darkbot.protocol.WriteablePacket;

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
