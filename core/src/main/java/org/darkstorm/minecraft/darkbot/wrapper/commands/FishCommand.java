package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.FishingTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class FishCommand extends AbstractCommand {

	public FishCommand(MinecraftBotWrapper bot) {
		super(bot, "fish", "Activate the fishing task");
	}

	@Override
	public void execute(String[] args) {
		FishingTask task = bot.getTaskManager().getTaskFor(FishingTask.class);
		if(task.isActive()) {
			task.stop();
			controller.say("No longer fishing.");
		} else {
			task.start();
			controller.say("Now fishing!");
		}
	}
}
