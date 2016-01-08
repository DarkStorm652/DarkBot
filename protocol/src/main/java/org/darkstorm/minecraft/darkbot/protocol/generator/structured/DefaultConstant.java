package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public class DefaultConstant implements Constant {
    private final String name;
    private final String className;
    private final Code code;

    public DefaultConstant(String name, String className, Code code) {
        this.name = name;
        this.className = className;
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public Code getInitializerCode() {
        return code;
    }
}
