package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.Collection;

public class DefaultHeader extends DefaultCompound implements Header {
    private final Field idField;

    public DefaultHeader(Collection<Field> fields, String idFieldName, Code readCode, Code writeCode) {
        super(fields, readCode, writeCode);

        this.idField = getField(idFieldName);
    }

    @Override
    public Field getIdField() {
        return idField;
    }
}
