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

		String[] botArgs = Arrays.copyOfRange(args, end, args.length);

		CLIBotWrapper.main(botArgs);
	}
}
