package org.darkstorm.minecraft.darkbot.protocol.type;

import java.util.Collection;

public interface TypeModelRegistry {
	public TypeModel<?> getTypeModel(String name);
	public void register(TypeModel<?> typeModel);
	public void unregister(TypeModel<?> typeModel);
	public Collection<TypeModel<?>> getTypeModels();
}
