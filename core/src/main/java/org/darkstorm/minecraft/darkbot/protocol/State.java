package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;

public interface State {
	public String getName();
	public Protocol getProtocol();
	public boolean isActive();
	
	public Packet read(InputStream stream) throws IOException;
}
