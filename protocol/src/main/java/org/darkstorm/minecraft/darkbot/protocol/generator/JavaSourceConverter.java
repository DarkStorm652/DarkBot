package org.darkstorm.minecraft.darkbot.protocol.generator;

public final class JavaSourceConverter implements SourceConverter {
    @Override
    public String convertSource(String source) {
        return source;
    }

    @Override
    public String getSupportedSourceType() {
        return "java";
    }
}
