package org.darkstorm.minecraft.darkbot.protocol.type;

import java.util.*;

abstract class AbstractTypeBuilder<T> implements TypeBuilder<T> {
	private final Set<Option<?>> validOptions;
	private final Map<Option<?>, Object> options = new HashMap<>();
	
	protected AbstractTypeBuilder(Collection<Option<?>> validOptions) {
		this.validOptions = new HashSet<>(validOptions);
	}
	
	protected AbstractTypeBuilder(Collection<Option<?>> validOptions, Map<Option<?>, Object> defaultValues) {
		this(validOptions);
		
		options.putAll(defaultValues);
	}
	
	@Override
	public final <S> TypeBuilder<T> withOption(Option<S> option, S value) {
		if(!validOptions.contains(options))
			throw new IllegalArgumentException("Type option '" + option.getName() + "' unsupported");
		
		options.put(option, option.getType().castValue(value));
		return this;
	}
	
	protected final Map<Option<?>, Object> getOptions() {
		for(Option<?> option : validOptions)
			if(!options.containsKey(option))
				throw new IllegalStateException("Missing required option '" + option + "'");
		return new HashMap<>(options);
	}
}
