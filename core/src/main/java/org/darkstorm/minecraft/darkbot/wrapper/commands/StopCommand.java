package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class StopCommand extends AbstractCommand {

	public StopCommand(MinecraftBotWrapper bot) {
		super(bot, "stop", "Stop all tasks and activities");
	}

	@Override
	public void execute(String[] args) {
		bot.getTaskManager().stopAll();
		bot.setActivity(null);
		controller.say("Stopped all tasks.");
	}
}
