package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.util.*;

public class DefaultProtocolFamily implements ProtocolFamily {
    private final String name;
    private final Compound header;
    private final Map<String, TypeModel> models;

    public DefaultProtocolFamily(String name, Compound header, Collection<TypeModel> models) {
        this.name = name;
        this.header = header;

        Map<String, TypeModel> modelMap = new HashMap<>();
        for(TypeModel model : models)
            if(modelMap.put(model.getName(), model) != null)
                throw new IllegalArgumentException("Duplicate model '" + model.getName() + "' on family '" + name + "'");
        this.models = Collections.unmodifiableMap(modelMap);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Compound getHeaderCompound() {
        return header;
    }

    @Override
    public TypeModel getTypeModel(String name) {
        return models.get(name);
    }

    @Override
    public Collection<TypeModel> getTypeModels() {
        return models.values();
    }
}
