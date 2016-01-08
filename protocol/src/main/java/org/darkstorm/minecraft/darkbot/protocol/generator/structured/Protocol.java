package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public interface Protocol {
	public String getVersionName();
	public Set<String> getVersionAliases();
	
	public Collection<State> getStates();
	public State getState(String name);
	public State getInitialState();

	public ProtocolFamily getFamily();
}
