package org.darkstorm.minecraft.darkbot.protocol.generator.type;

import java.util.Collection;

public interface TypeModelRegistry {
	public TypeModel<?> getTypeModel(String name);
	public void register(TypeModel<?> typeModel);
	public void unregister(TypeModel<?> typeModel);
	public Collection<TypeModel<?>> getTypeModels();
}
