package org.darkstorm.minecraft.darkbot.protocol;

import org.darkstorm.minecraft.darkbot.protocol.type.Type;

public interface Field<T> {
	public String getName();
	public Type<T> getType();
	public T getValue();
}
