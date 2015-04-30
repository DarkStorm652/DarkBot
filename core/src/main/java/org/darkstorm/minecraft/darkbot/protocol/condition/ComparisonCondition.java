package org.darkstorm.minecraft.darkbot.protocol.condition;

import org.darkstorm.minecraft.darkbot.protocol.FieldModel;

public interface ComparisonCondition extends Condition {
	public FieldModel getField();
	public Object getValue();
}
