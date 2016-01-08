package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.Collection;

public interface State {
	public String getName();
	public Protocol getProtocol();
	public Collection<Packet> getPackets(Direction direction);
	public Packet getPacket(Direction direction, String name);
	public Packet getPacket(Direction direction, int id);
}
