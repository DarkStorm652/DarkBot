package org.darkstorm.minecraft.darkbot.wrapper.cli;

import org.darkstorm.minecraft.darkbot.protocol.ProtocolProvider;
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

    static ProtocolProvider getProtocolProvider(OptionSet options, OptionSpec<String> protocolOption) {
        if(options.has(protocolOption)) {
            String protocolString = options.valueOf(protocolOption);

            Collection<ProtocolProvider> providers = ProtocolProvider.getProviders().stream()
                    .filter(p -> protocolString.equals(p.getMinecraftVersion()))
                    .collect(Collectors.toList());
            if(providers.size() == 1) {
                return providers.iterator().next();
            } else if(providers.size() > 1) {
                System.out.println("Multiple protocol providers found for '" + protocolString + "'.");
                return null;
            } else {
                ProtocolProvider provider = null;
                try {
                    provider = Class.forName(protocolString).asSubclass(ProtocolProvider.class).newInstance();
                } catch(Throwable exception) {
                    exception.printStackTrace();
                }
                for(ProtocolProvider p : ProtocolProvider.getProviders()) {
                    if(protocolString.equals(p.getClass().getName())) {
                        provider = p;
                        break;
                    }
                }
                if(provider == null)
                    System.out.println("No protocol provider found for '" + protocolString + "'.");
                return provider;
            }
        } else {
            System.out.println("Protocol name required.");
            return null;
        }
    }

    static void dumpProtocols() {
        if(!ProtocolProvider.getProviders().isEmpty()) {
            System.out.println("Available protocols:");
            ProtocolProvider.getProviders().stream()
                    .map(ProtocolProvider::getMinecraftVersion)
                    .distinct()
                    .sorted()
                    .forEach(version -> {
                        System.out.println("  " + version);
                        ProtocolProvider.getProviders().stream()
                                .filter(provider -> version.equals(provider.getMinecraftVersion()))
                                .forEach(provider -> System.out.println("    " + provider.getClass().getName()));
                    });
        } else
            System.out.println("No available protocols. Ensure that protocol jars are placed in the relative 'protocols/' directory.");
    }
}
