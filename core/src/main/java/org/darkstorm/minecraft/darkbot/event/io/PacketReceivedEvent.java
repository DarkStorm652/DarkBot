package org.darkstorm.minecraft.darkbot.event.io;

import org.darkstorm.minecraft.darkbot.protocol.ReadablePacket;

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
