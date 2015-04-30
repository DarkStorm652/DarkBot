package org.darkstorm.minecraft.darkbot.protocol;

public interface State {
	public String getName();
	public Protocol getProtocol();
	public boolean isActive();
}
