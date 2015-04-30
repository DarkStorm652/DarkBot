package org.darkstorm.minecraft.darkbot.protocol.type;

import java.io.*;
import java.util.Collection;

import org.darkstorm.minecraft.darkbot.protocol.Packet;

public interface Type<T> {
	public String getName();
	public T castValue(Object value) throws IllegalArgumentException;
	public Collection<Option<?>> getOptions();
	public boolean hasOption(Option<?> option);
	public <S> S getOptionValue(Option<S> option);

	public T read(Packet packet, DataInputStream stream) throws IOException;
	public void write(Packet packet, DataOutputStream stream, T object) throws IOException;
}
