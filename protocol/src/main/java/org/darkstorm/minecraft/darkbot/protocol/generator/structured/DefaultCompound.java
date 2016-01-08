package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public class DefaultCompound implements Compound {
    private final Map<String, Constant> constants;
    private final Code importCode, readCode, writeCode;

    public DefaultCompound(Collection<Constant> constants, Code importCode, Code readCode, Code writeCode) {
        Map<String, Constant> constantMap = new HashMap<>();
        for(Constant constant : constants)
            if(constantMap.put(constant.getName(), constant) != null)
                throw new IllegalArgumentException("Duplicate constant '" + constant.getName() + "' for compound");
        this.constants = Collections.unmodifiableMap(constantMap);

        this.importCode = importCode;
        this.readCode = readCode;
        this.writeCode = writeCode;
    }

    @Override
    public Collection<Constant> getConstants() {
        return constants.values();
    }

    @Override
    public Constant getConstant(String name) {
        return constants.get(name);
    }

    @Override
    public Code getImportCode() {
        return importCode;
    }

    @Override
    public Code getReadCode() {
        return readCode;
    }

    @Override
    public Code getWriteCode() {
        return writeCode;
    }
}
