package org.darkstorm.minecraft.darkbot.protocol.generator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SourceGeneratorImpl implements SourceGenerator {
    private final Map<String, SourceConverter> converters;

    public SourceGeneratorImpl(SourceConverter... converters) {
        Map<String, SourceConverter> converterMap = new HashMap<>();
        for(SourceConverter converter : converters) {
            SourceConverter prev = converterMap.put(converter.getSupportedSourceType(), converter);
            if(prev != null)
                throw new IllegalArgumentException("SourceConverter collision for source type '"
                        + converter.getSupportedSourceType() + "'");
        }
        this.converters = Collections.unmodifiableMap(converterMap);
    }

    @Override
    public SourceSet generateSources(ProtocolSet protocolSet) {
        // TODO: Implement
        return null;
    }

    @Override
    public Set<String> getSupportedSourceTypes() {
        return converters.keySet();
    }
}
