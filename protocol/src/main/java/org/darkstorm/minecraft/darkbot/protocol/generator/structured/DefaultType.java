package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.io.*;
import java.util.*;

public class DefaultType implements Type {
    private final TypeModel model;
    private final Map<TypeOption, TypeOptionValue> options;

    public DefaultType(TypeModel model, Map<TypeOption, TypeOptionValue> options) {
        this.model = model;
        this.options = new HashMap<>(options);
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public TypeOptionValue getOptionValue(TypeOption option) {
        return options.get(option);
    }

    @Override
    public TypeModel getModel() {
        return model;
    }
}
