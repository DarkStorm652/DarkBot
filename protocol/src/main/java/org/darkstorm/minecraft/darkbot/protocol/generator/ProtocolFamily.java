package org.darkstorm.minecraft.darkbot.protocol.generator;

import java.io.*;

import org.darkstorm.minecraft.darkbot.protocol.generator.type.TypeModelRegistry;

public interface ProtocolFamily {
	public Compound readHeader(InputStream stream) throws IOException;
	
	public TypeModelRegistry getTypeModelRegistry();
}
