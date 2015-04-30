package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.FishingTask;

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
