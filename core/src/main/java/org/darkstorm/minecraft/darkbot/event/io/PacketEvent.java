package org.darkstorm.minecraft.darkbot.event.io;

import org.darkstorm.minecraft.darkbot.event.AbstractEvent;
import org.darkstorm.minecraft.darkbot.protocol.Packet;

public abstract class PacketEvent extends AbstractEvent {
	public abstract Packet getPacket();
}
