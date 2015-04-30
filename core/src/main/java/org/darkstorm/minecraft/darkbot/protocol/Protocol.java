package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;
import java.util.Collection;

public interface Protocol {
	public int getVersion();
	public ProtocolType getType();
	
	public Collection<State> getStates();
	public State getState(String name);
	public State getCurrentState();
	
	public Packet read(InputStream stream) throws IOException;
	public void write(OutputStream stream, Packet packet) throws IOException;
}
