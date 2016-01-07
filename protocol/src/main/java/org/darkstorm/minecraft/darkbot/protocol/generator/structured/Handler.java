package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public interface Handler {
	public String getPacketName();
	public Direction getDirection();
	public void handle(Packet packet);
}
