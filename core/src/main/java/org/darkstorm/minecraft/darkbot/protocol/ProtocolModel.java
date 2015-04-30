package org.darkstorm.minecraft.darkbot.protocol;

import java.util.*;

import org.darkstorm.minecraft.darkbot.protocol.type.TypeModelRegistry;

public interface ProtocolModel {
	public String getVersionName();
	public Set<String> getVersionAliases();
	
	public Collection<StateModel> getStates();
	public StateModel getState(String name);
	public StateModel getInitialState();
	
	public TypeModelRegistry getTypeModelRegistry();
}
