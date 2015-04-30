package org.darkstorm.minecraft.darkbot.protocol.type;

import java.util.*;

abstract class AbstractType<T> implements Type<T> {
	private final String name;
	private final Map<Option<?>, ?> options;
	
	public AbstractType(String name, Map<Option<?>, Object> options) {
		this.name = name;
		
		Map<Option<?>, Object> newOptions = new HashMap<>();
		for(Map.Entry<Option<?>, ?> entry : options.entrySet()) {
			Option<?> option = entry.getKey();
			Object value = entry.getValue();
			newOptions.put(option, option.getType().castValue(value));
		}
		this.options = Collections.unmodifiableMap(options);
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final Collection<Option<?>> getOptions() {
		return options.keySet();
	}

	@Override
	public final boolean hasOption(Option<?> option) {
		return options.containsKey(option);
	}

	@Override
	public final <S> S getOptionValue(Option<S> option) {
		Object value = options.get(option);
		if(value == null)
			throw new IllegalArgumentException("Option not present");
		
		return option.getType().castValue(value);
	}
}
