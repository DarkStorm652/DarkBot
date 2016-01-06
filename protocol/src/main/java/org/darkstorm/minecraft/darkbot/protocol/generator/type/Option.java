package org.darkstorm.minecraft.darkbot.protocol.generator.type;


public interface Option<T> {
	public String getName();
	public Type<T> getType();
}