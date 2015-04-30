package org.darkstorm.darkbot.minecraftbot.event.io;

import org.darkstorm.darkbot.minecraftbot.protocol.ReadablePacket;

public class PacketReceivedEvent extends PacketEvent {
	private final ReadablePacket packet;

	public PacketReceivedEvent(ReadablePacket packet) {
		this.packet = packet;
	}

	@Override
	public ReadablePacket getPacket() {
		return packet;
	}
}
