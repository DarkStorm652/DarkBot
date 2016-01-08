package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public interface TypeModel {
	public String getName();
	public String getTypeClass();

	public Collection<TypeOption> getOptions();
	public TypeOption getOption(String name);

	public Collection<Constant> getConstants();
	public Constant getConstant(String name);

	public TypeBuilder createBuilder();

	public Code getReadCode();
	public Code getWriteCode();
}
