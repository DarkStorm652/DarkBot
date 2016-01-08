package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.List;

public interface DefaultCodeProducer {
    public Code produceObjectCompoundReadCode(List<Field> fields);
    public Code produceObjectCompoundWriteCode(List<Field> fields);
    public Code produceCompoundReadCode();
    public Code produceCompoundWriteCode();
}
