package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.Util;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class SayCommand extends AbstractCommand {

	public SayCommand(MinecraftBotWrapper bot) {
		super(bot, "say", "Send a message", "<message>", ".*");
	}

	@Override
	public void execute(String[] args) {
		bot.say(Util.join(args, " "));
	}
}
