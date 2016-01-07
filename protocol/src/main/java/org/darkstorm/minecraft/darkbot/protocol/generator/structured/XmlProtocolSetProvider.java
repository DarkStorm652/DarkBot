package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import org.darkstorm.minecraft.darkbot.protocol.generator.ProtocolSet;
import org.darkstorm.minecraft.darkbot.protocol.generator.ProtocolSetProvider;

import java.io.Reader;

public class XmlProtocolSetProvider implements ProtocolSetProvider {
    private final ProtocolSet protocolSet;

    public XmlProtocolSetProvider(Reader familySource, Reader... protocolSources) {

    }

    private ProtocolFamily readFamily(Reader source) {

    }

    @Override
    public ProtocolSet getProtocolSet() {
        return protocolSet;
    }
}
