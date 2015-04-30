package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.cli.CLISpamBotWrapper;
import org.darkstorm.darkbot.minecraftbot.util.Util;

public class SpamCommand extends AbstractCommand {

	public SpamCommand(CLISpamBotWrapper bot) {
		super(bot, "spam", "Start all bots spamming a message", "<message>", ".*");
	}

	@Override
	public void execute(String[] args) {
		CLISpamBotWrapper.setSpamMessage(Util.join(args, " "));
	}
}
