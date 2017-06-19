package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class ChatDelayCommand extends AbstractCommand {

	public ChatDelayCommand(MinecraftBotWrapper bot) {
		super(bot, "chatdelay", "Change chat delay", "<delay>", "[0-9]+");
	}

	@Override
	public void execute(String[] args) {
		controller.say("Set chat delay!");
		bot.setMessageDelay(Integer.parseInt(args[0]));
	}
}
