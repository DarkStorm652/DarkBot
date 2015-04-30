package org.darkstorm.minecraft.darkbot.event.io;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;
import org.darkstorm.minecraft.darkbot.protocol.Packet;

public abstract class PacketEvent extends AbstractEvent {
	private final Packet packet;
	
	public PacketEvent(Packet packet) {
		this.packet = packet;
	}
	
	public final Packet getPacket() {
		return packet;
	}
}
