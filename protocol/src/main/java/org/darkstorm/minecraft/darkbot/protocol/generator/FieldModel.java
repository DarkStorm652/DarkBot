package org.darkstorm.minecraft.darkbot.protocol.generator;

import java.io.*;

import org.darkstorm.minecraft.darkbot.protocol.generator.type.Type;

public interface FieldModel<T> {
	public String getName();
	public Type<T> getType();
	
	public Field<T> read(InputStream stream) throws IOException;
}
