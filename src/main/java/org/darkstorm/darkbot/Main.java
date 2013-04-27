package org.darkstorm.darkbot;

import java.lang.reflect.Method;
import java.util.Arrays;

import joptsimple.*;

import org.darkstorm.darkbot.tools.*;
import org.darkstorm.darkbot.tools.ClassRepository.BotInfo;

public class Main {
	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("b", "bot")).withRequiredArg()
				.ofType(String.class).describedAs("botname");
		parser.acceptsAll(Arrays.asList("d", "debug"));
		parser.acceptsAll(Arrays.asList("h", "help"));
		try {
			Class<?> abstractOptionSpec = Class
					.forName("joptsimple.AbstractOptionSpec");
			Method recognizeMethod = OptionParser.class.getDeclaredMethod(
					"recognize", abstractOptionSpec);
			recognizeMethod.setAccessible(true);
			for(BotInfo info : ClassRepository.getBots())
				for(OptionSpec<?> argument : info.getArguments())
					if(argument != null
							&& abstractOptionSpec.isInstance(argument))
						recognizeMethod.invoke(parser,
								abstractOptionSpec.cast(argument));
		} catch(Throwable exception) {
			exception.printStackTrace();
		}
		OptionSet options;
		try {
			options = parser.parse(args);
		} catch(OptionException exception) {
			try {
				parser.printHelpOn(System.out);
			} catch(Exception exception1) {
				exception1.printStackTrace();
			}
			return;
		}
		if(options.has("help")) {
			try {
				parser.printHelpOn(System.out);
			} catch(Exception exception1) {
				exception1.printStackTrace();
			}
			return;
		}
		new DarkBot(options);
	}
}
