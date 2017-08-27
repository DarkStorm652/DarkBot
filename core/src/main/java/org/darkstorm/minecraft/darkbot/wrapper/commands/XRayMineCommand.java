package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.MiningTask;
import org.darkstorm.minecraft.darkbot.ai.XRayMiningTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class XRayMineCommand extends AbstractCommand {

	public XRayMineCommand(MinecraftBotWrapper bot) {
		super(bot, "xraymine", "Activate the XRay mining task");
	}

	@Override
	public void execute(String[] args) {
		XRayMiningTask task = bot.getTaskManager().getTaskFor(XRayMiningTask.class);
		if(task.isActive()) {
			task.stop();
			controller.say("No longer mining.");
		} else {
			task.start();
			controller.say("Now mining!");
		}
	}
}
