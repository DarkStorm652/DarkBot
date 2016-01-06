package org.darkstorm.minecraft.darkbot.protocol.generator;

public interface State {
	public String getName();
	public Protocol getProtocol();
	public boolean isActive();
}
