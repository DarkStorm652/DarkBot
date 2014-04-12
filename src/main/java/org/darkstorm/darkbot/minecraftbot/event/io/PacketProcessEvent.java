package org.darkstorm.darkbot.minecraftbot.event.io;

import org.darkstorm.darkbot.minecraftbot.protocol.ReadablePacket;

public class PacketProcessEvent extends PacketEvent {
	private final ReadablePacket packet;

	public PacketProcessEvent(ReadablePacket packet) {
		this.packet = packet;
	}

	@Override
	public ReadablePacket getPacket() {
		return packet;
	}

}
