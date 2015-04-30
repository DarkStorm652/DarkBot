package org.darkstorm.minecraft.darkbot.event.io;

import org.darkstorm.minecraft.darkbot.protocol.*;

public class PacketProcessEvent extends PacketEvent {
	public PacketProcessEvent(Packet packet) {
		super(packet);
		
		if(!packet.getDirection().equals(Direction.TO_CLIENT))
			throw new IllegalArgumentException("Wrong-way packet");
	}
}
