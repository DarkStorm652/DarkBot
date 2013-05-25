package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.ChopTreesTask;

public class ChopCommand extends AbstractCommand {

	public ChopCommand(DarkBotMC bot) {
		super(bot, "chop", "Chop down nearby trees");
	}

	@Override
	public void execute(String[] args) {
		bot.getTaskManager().getTaskFor(ChopTreesTask.class).start();
		controller.say("Now chopping!");
	}
}
