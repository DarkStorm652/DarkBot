package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;
import java.util.*;

public interface Protocol {
	public String getVersionName();
	public Set<String> getVersionAliases();
	
	public Collection<State> getStates();
	public State getState(String name);
	public State getCurrentState();
	
	public Packet read(InputStream stream) throws IOException;
	public void write(OutputStream stream, Packet packet) throws IOException;
}
