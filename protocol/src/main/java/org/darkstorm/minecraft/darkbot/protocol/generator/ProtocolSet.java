package org.darkstorm.minecraft.darkbot.protocol.generator;

import org.darkstorm.minecraft.darkbot.protocol.generator.structured.ProtocolFamily;

import java.util.List;

public interface ProtocolSet {
    public ProtocolFamily getFamily();
    public List<Protocol> getProtocols();
}
