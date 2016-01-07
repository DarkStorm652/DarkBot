package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public class DefaultTypeModelRegistry implements TypeModelRegistry {
	private final Map<String, TypeModel<?>> typeModels = new HashMap<>();

	@Override
	public TypeModel<?> getTypeModel(String name) {
		return typeModels.get(name);
	}

	@Override
	public void register(TypeModel<?> typeModel) {
		typeModels.put(typeModel.getName(), typeModel);
	}

	@Override
	public void unregister(TypeModel<?> typeModel) {
		TypeModel<?> current = typeModels.get(typeModel.getName());
		if(current != null && typeModel.equals(current))
			typeModels.remove(typeModel.getName());
	}

	@Override
	public Collection<TypeModel<?>> getTypeModels() {
		return new ArrayList<>(typeModels.values());
	}
}
