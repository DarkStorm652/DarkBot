package org.darkstorm.minecraft.darkbot.protocol.generator;

import java.util.Set;

public interface SourceGenerator {
    public SourceSet generateSources(ProtocolSet protocolSet);
    public Set<String> getSupportedSourceTypes();
}
