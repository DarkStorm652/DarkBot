package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public interface TypeBuilder {
    public TypeBuilder withOption(TypeOption option, TypeOptionValue value) throws IllegalArgumentException;
	public Type build();
}