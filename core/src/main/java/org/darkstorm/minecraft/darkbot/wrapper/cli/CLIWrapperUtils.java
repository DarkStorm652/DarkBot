package org.darkstorm.minecraft.darkbot.wrapper.cli;

import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import joptsimple.*;

class CLIWrapperUtils {

    static OptionSet parseOptions(OptionParser parser, String[] args) {
        try {
            return parser.parse(args);
        } catch(OptionException exception) {
            printHelp(parser);
            System.exit(1);
            throw new RuntimeException();
        }
    }

    static void printHelp(OptionParser parser) {
        try {
            parser.printHelpOn(System.out);
        } catch(Exception exception) {}
    }

    static <T> T getRequiredOption(OptionSet options, OptionSpec<T> option) {
        if(!options.has(option)) {
            String name;
            if(option.options().size() > 1)
                name = option.options().stream().max(Comparator.comparingInt(String::length)).get();
            else
                name = option.options().iterator().next();
            System.out.println("Option '" + name + "' required.");
            System.exit(1);
            return null;
        } else
            return options.valueOf(option);
    }
}
