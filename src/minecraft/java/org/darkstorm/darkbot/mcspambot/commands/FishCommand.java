package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.FishingTask;

public class FishCommand extends AbstractCommand {

	public FishCommand(DarkBotMC bot) {
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
