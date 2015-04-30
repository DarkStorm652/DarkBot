package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.ChopTreesTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class ChopCommand extends AbstractCommand {

	public ChopCommand(MinecraftBotWrapper bot) {
		super(bot, "chop", "Chop down nearby trees");
	}

	@Override
	public void execute(String[] args) {
		bot.getTaskManager().getTaskFor(ChopTreesTask.class).start();
		controller.say("Now chopping!");
	}
}
