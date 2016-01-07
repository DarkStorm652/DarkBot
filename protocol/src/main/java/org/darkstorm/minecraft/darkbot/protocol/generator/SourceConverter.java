package org.darkstorm.minecraft.darkbot.protocol.generator;

public interface SourceConverter {
    public String convertSource(String source);
    public String getSupportedSourceType();
}
