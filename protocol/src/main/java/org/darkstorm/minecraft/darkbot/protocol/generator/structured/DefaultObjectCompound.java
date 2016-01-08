package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public class DefaultObjectCompound extends DefaultCompound implements ObjectCompound {
    private final Map<String, Field> fields;

    public DefaultObjectCompound(Collection<Field> fields, Collection<Constant> constants,
                                 Code importCode, Code readCode, Code writeCode) {
        super(constants, importCode, readCode, writeCode);

        Map<String, Field> fieldMap = new HashMap<>();
        for(Field field : fields)
            if(fieldMap.put(field.getName(), field) != null)
                throw new IllegalArgumentException("Duplicate field '" + field.getName() + "' for compound");
        this.fields = Collections.unmodifiableMap(fieldMap);
    }

    @Override
    public Collection<Field> getFields() {
        return fields.values();
    }

    @Override
    public Field getField(String name) {
        return fields.get(name);
    }
}
