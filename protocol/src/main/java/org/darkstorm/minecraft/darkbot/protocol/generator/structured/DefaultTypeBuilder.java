package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public class DefaultTypeBuilder implements TypeBuilder {
    private final TypeModel model;
    private final Map<TypeOption, TypeOptionValue> options = new HashMap<>();

    public DefaultTypeBuilder(TypeModel model) {
        this.model = model;

        for(TypeOption option : model.getOptions())
            if(option.hasDefaultValue())
                options.put(option, option.getDefaultValue());
    }

    @Override
    public TypeBuilder withOption(TypeOption option, TypeOptionValue value) throws IllegalArgumentException {
        if(!model.getOptions().contains(option))
            throw new IllegalArgumentException("Option does not apply to type model");

        options.put(option, value);
        return this;
    }

    @Override
    public Type build() {
        for(TypeOption option : model.getOptions())
            if(!options.containsKey(option))
                throw new IllegalStateException("Option '" + option.getName() + "' not supplied");

        return new DefaultType(model, options);
    }
}
