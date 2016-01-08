package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public class DefaultTypeOption implements TypeOption {
    private final String name;
    private final String type;
    private final String defaultValue;

    public DefaultTypeOption(String name, String type) {
        this(name, type, null);
    }

    public DefaultTypeOption(String name, String type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }
}
