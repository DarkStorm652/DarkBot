package org.darkstorm.minecraft.darkbot.event.io;

import org.darkstorm.minecraft.darkbot.protocol.*;

public class PacketSentEvent extends PacketEvent {
	public PacketSentEvent(Packet packet) {
		super(packet);
		
		if(!packet.getDirection().equals(Direction.TO_SERVER))
			throw new IllegalArgumentException("Wrong-way packet");
	}
}
