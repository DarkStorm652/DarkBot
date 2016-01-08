package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public class TypeTypeOptionValue implements TypeOptionValue {
    private final TypeOption option;
    private final Type value;

    public TypeTypeOptionValue(TypeOption option, Type value) {
        this.option = option;
        this.value = value;
    }

    @Override
    public TypeOption getOption() {
        return option;
    }

    public Type getValue() {
        return value;
    }
}
