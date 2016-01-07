package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.io.*;

import org.darkstorm.minecraft.darkbot.protocol.generator.Compound;

public interface ProtocolFamily {
	public Compound readHeader(InputStream stream) throws IOException;
	
	public TypeModelRegistry getTypeModelRegistry();
}
