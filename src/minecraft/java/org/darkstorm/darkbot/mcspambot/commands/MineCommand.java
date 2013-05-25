package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.MiningTask;

public class MineCommand extends AbstractCommand {

	public MineCommand(DarkBotMC bot) {
		super(bot, "mine", "Activate the mining task");
	}

	@Override
	public void execute(String[] args) {
		MiningTask task = bot.getTaskManager().getTaskFor(MiningTask.class);
		if(task.isActive()) {
			task.stop();
			controller.say("No longer mining.");
		} else {
			task.start();
			controller.say("Now mining!");
		}
	}
}
