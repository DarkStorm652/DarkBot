package org.darkstorm.darkbot.minecraftbot.events.io;

import org.darkstorm.darkbot.minecraftbot.events.Event;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;

public abstract class PacketEvent extends Event {
	public abstract Packet getPacket();
}
