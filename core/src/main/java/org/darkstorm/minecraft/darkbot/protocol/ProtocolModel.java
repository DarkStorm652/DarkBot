package org.darkstorm.minecraft.darkbot.protocol;

import java.util.Collection;

import org.darkstorm.minecraft.darkbot.protocol.type.TypeModelRegistry;

public interface ProtocolModel {
	public int getVersion();
	public ProtocolType getType();
	
	public Collection<StateModel> getStates();
	public StateModel getState(String name);
	public StateModel getInitialState();
	
	public TypeModelRegistry getTypeModelRegistry();
}
