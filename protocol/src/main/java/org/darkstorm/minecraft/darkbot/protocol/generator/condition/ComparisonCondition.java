package org.darkstorm.minecraft.darkbot.protocol.generator.condition;

import org.darkstorm.minecraft.darkbot.protocol.generator.FieldModel;

public interface ComparisonCondition extends Condition {
	public FieldModel<?> getField();
	public Object getValue();
}
