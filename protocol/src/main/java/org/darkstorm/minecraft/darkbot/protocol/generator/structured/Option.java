package org.darkstorm.minecraft.darkbot.protocol.generator.structured;


import org.darkstorm.minecraft.darkbot.protocol.generator.type.Type;

public interface Option<T> {
	public String getName();
	public Type<T> getType();
}