package org.darkstorm.minecraft.darkbot.protocol;

import java.util.Collection;

public interface Protocol {
	public int getVersion();
	public ProtocolType getType();
	
	public Collection<State> getStates();
	public State getState(String name);
	public State getCurrentState();
}
