package org.darkstorm.minecraft.darkbot.event.io;

import org.darkstorm.minecraft.darkbot.protocol.*;

public class PacketReceivedEvent extends PacketEvent {
	public PacketReceivedEvent(Packet packet) {
		super(packet);
		
		if(!packet.getDirection().equals(Direction.TO_CLIENT))
			throw new IllegalArgumentException("Wrong-way packet");
	}
}
