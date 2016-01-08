package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.Collection;

public interface ObjectCompound extends Compound {
    public Collection<Field> getFields();
    public Field getField(String name);
}
