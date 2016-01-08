package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.io.*;
import java.util.Collection;

public interface Type {
	public String getName();
	public TypeOptionValue getOptionValue(TypeOption option);

	public TypeModel getModel();
}