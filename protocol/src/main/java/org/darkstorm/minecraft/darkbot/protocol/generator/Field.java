package org.darkstorm.minecraft.darkbot.protocol.generator;

import org.darkstorm.minecraft.darkbot.protocol.generator.type.Type;

public interface Field<T> {
	public String getName();
	public Type<T> getType();
	public T getValue();
}
