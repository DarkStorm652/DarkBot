package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import org.darkstorm.minecraft.darkbot.protocol.generator.type.Type;

abstract class AbstractOption<T> implements Option<T> {
	private final String name;
	private final Type<T> type;
	
	protected AbstractOption(String name, Type<T> type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public final String getName() {
		return name;
	}
	
	@Override
	public final Type<T> getType() {
		return type;
	}
}