package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMCSpambot;
import org.darkstorm.darkbot.minecraftbot.util.Util;

public class SpamCommand extends AbstractCommand {

	public SpamCommand(DarkBotMCSpambot bot) {
		super(bot, "spam", "Start all bots spamming a message", "<message>", ".*");
	}

	@Override
	public void execute(String[] args) {
		DarkBotMCSpambot.setSpamMessage(Util.join(args, " "));
	}
}
