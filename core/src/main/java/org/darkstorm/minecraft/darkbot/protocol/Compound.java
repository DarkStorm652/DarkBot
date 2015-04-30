package org.darkstorm.minecraft.darkbot.protocol;

import java.util.Collection;

public interface Compound {
	public Collection<Field<?>> getFields();
	public Field<?> getField(String name);
}
