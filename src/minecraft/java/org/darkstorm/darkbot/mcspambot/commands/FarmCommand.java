package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.FarmingTask;

public class FarmCommand extends AbstractCommand {

	public FarmCommand(DarkBotMC bot) {
		super(bot, "farm", "Activate the farming task");
	}

	@Override
	public void execute(String[] args) {
		FarmingTask task = bot.getTaskManager().getTaskFor(FarmingTask.class);
		if(task.isActive()) {
			task.stop();
			controller.say("No longer farming.");
		} else {
			task.start();
			controller.say("Now farming!");
		}
	}
}
