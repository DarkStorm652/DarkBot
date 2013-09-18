package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
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
			controller.say("/r " + "No longer fishing.");
		} else {
			task.start();
			controller.say("/r " + "Now fishing!");
		}
	}
}
