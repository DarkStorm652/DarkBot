package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import org.darkstorm.minecraft.darkbot.protocol.generator.type.TypeBuilder;

import java.util.*;

public interface TypeModel<T> {
	public String getName();
	public Class<T> getTypeClass();
	public Collection<Option<?>> getOptions();
	public Option<?> getOption(String name);
	
	public TypeBuilder<T> createBuilder();
}
