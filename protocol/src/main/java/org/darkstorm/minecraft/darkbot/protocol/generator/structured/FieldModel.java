package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.io.*;

import org.darkstorm.minecraft.darkbot.protocol.generator.Field;
import org.darkstorm.minecraft.darkbot.protocol.generator.type.Type;

public interface FieldModel<T> {
	public String getName();
	public Type<T> getType();
	
	public Field<T> read(InputStream stream) throws IOException;
}
