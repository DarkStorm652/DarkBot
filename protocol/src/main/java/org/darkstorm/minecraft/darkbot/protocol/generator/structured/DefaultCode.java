package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

public class DefaultCode implements Code {
    private final String source, sourceType;

    public DefaultCode(String source, String sourceType) {
        this.source = source;
        this.sourceType = sourceType;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getSourceType() {
        return sourceType;
    }
}
