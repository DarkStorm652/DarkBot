package org.darkstorm.minecraft.darkbot.protocol.type;

import java.io.*;
import java.util.Collection;

public interface Type<T> {
	public String getName();
	public T castValue(Object value) throws IllegalArgumentException;
	public Collection<Option<?>> getOptions();
	public Option<?> getOption(String name);
	public <S> S getOptionValue(Option<S> option);

	public T read(InputStream stream) throws IOException;
	public void write(InputStream stream, T object) throws IOException;
}
