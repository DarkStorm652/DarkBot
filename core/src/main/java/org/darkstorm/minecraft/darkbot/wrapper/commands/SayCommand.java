package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.util.Util;

public class SayCommand extends AbstractCommand {

	public SayCommand(MinecraftBotWrapper bot) {
		super(bot, "say", "Send a message", "<message>", ".*");
	}

	@Override
	public void execute(String[] args) {
		bot.say(Util.join(args, " "));
	}
}
