package org.darkstorm.minecraft.darkbot.protocol.type;

public interface TypeBuilder<T> {
	public <S> TypeBuilder<T> withOption(Option<S> option, S value);
	public Type<T> build();
}
