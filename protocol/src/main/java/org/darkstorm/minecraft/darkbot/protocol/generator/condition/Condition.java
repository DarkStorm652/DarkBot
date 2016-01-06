package org.darkstorm.minecraft.darkbot.protocol.generator.condition;

import org.darkstorm.minecraft.darkbot.protocol.generator.Compound;

public interface Condition {
	public boolean evaluate(Compound compound);
}
