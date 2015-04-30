package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.MiningTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

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
