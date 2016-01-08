package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public class DefaultTypeModel implements TypeModel {
    private final String name;
    private final String typeClass;
    private final Map<String, TypeOption> options;
    private final Map<String, Constant> constants;

    private final Code readCode, writeCode;

    public DefaultTypeModel(String name, String typeClass,
                            Collection<TypeOption> options, Collection<Constant> constants,
                            Code readCode, Code writeCode) {
        this.name = name;
        this.typeClass = typeClass;

        Map<String, TypeOption> optionMap = new HashMap<>();
        for(TypeOption option : options)
            if(optionMap.put(option.getName(), option) != null)
                throw new IllegalArgumentException("Duplicate option '" + option.getName() + "' on model '" + name + "'");
        this.options = Collections.unmodifiableMap(optionMap);

        Map<String, Constant> constantMap = new HashMap<>();
        for(Constant constant : constants)
            if(constantMap.put(constant.getName(), constant) != null)
                throw new IllegalArgumentException("Duplicate constant '" + constant.getName() + "' on model '" + name + "'");
        this.constants = Collections.unmodifiableMap(constantMap);

        this.readCode = readCode;
        this.writeCode = writeCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeClass() {
        return typeClass;
    }

    @Override
    public Collection<TypeOption> getOptions() {
        return options.values();
    }

    @Override
    public TypeOption getOption(String name) {
        return options.get(name);
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
    public TypeBuilder createBuilder() {
        return new DefaultTypeBuilder(this);
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
