package org.darkstorm.minecraft.darkbot.protocol.type;


public interface Option<T> {
	public String getName();
	public Type<T> getType();
}