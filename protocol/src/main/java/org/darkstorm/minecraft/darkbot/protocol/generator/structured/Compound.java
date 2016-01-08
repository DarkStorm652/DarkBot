package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.io.*;
import java.util.Collection;

public interface Compound {
	public Collection<Constant> getConstants();
	public Constant getConstant(String name);

	public Code getImportCode();
	public Code getReadCode();
	public Code getWriteCode();
}
