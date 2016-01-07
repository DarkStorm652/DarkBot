package org.darkstorm.minecraft.darkbot.protocol.generator;

import java.io.File;
import java.util.List;

public interface SourceSet {
    public List<File> getJavaSources();
    public List<File> getResources();
}
