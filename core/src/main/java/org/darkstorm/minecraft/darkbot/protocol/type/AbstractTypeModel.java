package org.darkstorm.minecraft.darkbot.protocol.type;

import java.util.*;

abstract class AbstractTypeModel<T> implements TypeModel<T> {
	private final String name;
	private final Map<String, Option<?>> options;
	
	public AbstractTypeModel(String name, Set<Option<?>> options) {
		this.name = name;
		
		Map<String, Option<?>> newOptions = new HashMap<>();
		for(Option<?> option : options)
			newOptions.put(option.getName(), option);
		this.options = Collections.unmodifiableMap(newOptions);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<Option<?>> getOptions() {
		return options.values();
	}

	@Override
	public Option<?> getOption(String name) {
		return options.get(name);
	}
}
