package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
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
