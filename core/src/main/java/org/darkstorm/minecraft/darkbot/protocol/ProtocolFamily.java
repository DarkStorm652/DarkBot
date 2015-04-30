package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;

import org.darkstorm.minecraft.darkbot.protocol.type.TypeModelRegistry;

public interface ProtocolFamily {
	public Compound readHeader(InputStream stream) throws IOException;
	
	public TypeModelRegistry getTypeModelRegistry();
}
