package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.MiningTask;

public class MineCommand extends AbstractCommand {

	public MineCommand(MinecraftBotWrapper bot) {
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
