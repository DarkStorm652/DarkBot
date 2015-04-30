package org.darkstorm.minecraft.darkbot.protocol;


public interface Packet extends Compound {
	public String getName();
	public int getId();
	public State getState();
	public Direction getDirection();
}
