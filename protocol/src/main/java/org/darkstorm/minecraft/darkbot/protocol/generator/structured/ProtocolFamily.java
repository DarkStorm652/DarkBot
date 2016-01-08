package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.io.*;
import java.util.Collection;

public interface ProtocolFamily {
	public String getName();

	public Compound getHeaderCompound();

	public TypeModel getTypeModel(String name);
	public Collection<TypeModel> getTypeModels();
}
