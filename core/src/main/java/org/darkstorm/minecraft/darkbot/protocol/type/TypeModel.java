package org.darkstorm.minecraft.darkbot.protocol.type;

import java.util.*;

public interface TypeModel<T> {
	public String getName();
	public Class<T> getTypeClass();
	public Collection<Option<?>> getOptions();
	public Option<?> getOption(String name);
	
	public TypeBuilder<T> createBuilder();
}
