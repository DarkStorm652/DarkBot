package org.darkstorm.minecraft.darkbot.protocol.generator.structured;


public interface Packet extends ObjectCompound {
	public String getName();
	public int getId();
	public State getState();
	public Direction getDirection();

}
