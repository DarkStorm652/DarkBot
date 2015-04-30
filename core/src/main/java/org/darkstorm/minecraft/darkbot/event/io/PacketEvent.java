package org.darkstorm.darkbot.minecraftbot.event.io;

import org.darkstorm.darkbot.minecraftbot.event.AbstractEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;

public abstract class PacketEvent extends AbstractEvent {
	public abstract Packet getPacket();
}
