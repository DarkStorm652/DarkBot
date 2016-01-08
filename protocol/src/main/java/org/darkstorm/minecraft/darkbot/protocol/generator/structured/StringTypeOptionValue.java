package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public class StringTypeOptionValue implements TypeOptionValue{
    private final TypeOption option;
    private final String value;

    public StringTypeOptionValue(TypeOption option, String value) {
        this.option = option;
        this.value = value;
    }

    @Override
    public TypeOption getOption() {
        return option;
    }

    public String getValue() {
        return value;
    }
}

