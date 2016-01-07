package org.darkstorm.minecraft.darkbot.protocol.generator.model;

import org.darkstorm.minecraft.darkbot.protocol.generator.Compound;

import java.io.*;
import java.util.Collection;

public interface CompoundModel {
	public Collection<FieldModel<?>> getFields();
	public FieldModel<?> getField(String name);
	
	public Compound read(InputStream stream) throws IOException;
}
