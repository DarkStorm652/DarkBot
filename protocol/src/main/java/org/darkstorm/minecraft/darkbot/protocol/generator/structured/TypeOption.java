package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public interface TypeOption {
	public String getName();
	public Type getType();

	public TypeOptionValue getDefaultValue();
	public boolean hasDefaultValue();
}