package org.darkstorm.minecraft.darkbot.wrapper;

import java.util.Arrays;

import org.darkstorm.minecraft.darkbot.wrapper.cli.*;

import joptsimple.*;

public class Main {
	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		OptionSpec<String> type = parser.acceptsAll(Arrays.asList("t", "type")).withRequiredArg().ofType(String.class).describedAs("botname");
		parser.acceptsAll(Arrays.asList("d", "debug"));
		parser.acceptsAll(Arrays.asList("h", "help"));

		int end = 0;
		for(int i = 0; i < Math.min(args.length, 2); i++) {
			if(args[i].equals("-t") || args[i].equalsIgnoreCase("--type")) {
				end = i + (i == args.length - 1 ? 1 : 2);
				break;
			}
		}
		String[] mainArgs = Arrays.copyOfRange(args, 0, end);
		String[] botArgs = Arrays.copyOfRange(args, end, args.length);

		OptionSet options;
		try {
			options = parser.parse(mainArgs);
		} catch(OptionException exception) {
			printHelp(parser);
			return;
		}
		if(options.has("help")) {
			printHelp(parser);
			return;
		}

		String value = "none";
		if(options.has(type))
			value = options.valueOf(type).toLowerCase();

		switch(value) {
		case "bot":
			CLIBotWrapper.main(botArgs);
			break;
		case "spambot":
			CLISpamBotWrapper.main(botArgs);
			break;
		case "gui":
			org.darkstorm.minecraft.darkbot.wrapper.gui.GUIBotWrapper.main(botArgs);
			break;
		default:
			printHelp(parser);
		}
	}

	private static void printHelp(OptionParser parser) {
		System.out.println("Available types:");
		System.out.println("\t* Bot");
		System.out.println("\t* Spambot");
		System.out.println("\t* GUI (beta)");
		System.out.println();
		try {
			parser.printHelpOn(System.out);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}
