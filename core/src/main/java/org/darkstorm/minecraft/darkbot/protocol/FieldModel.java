package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;

import org.darkstorm.minecraft.darkbot.protocol.condition.Condition;
import org.darkstorm.minecraft.darkbot.protocol.type.Type;

public interface FieldModel<T> {
	public String getName();
	public Type<T> getType();
	public Condition getCondition();
	
	public Field<T> read(InputStream stream) throws IOException;
}
