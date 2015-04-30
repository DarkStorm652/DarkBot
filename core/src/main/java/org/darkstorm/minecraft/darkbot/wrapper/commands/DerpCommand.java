package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.DerpTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class DerpCommand extends AbstractCommand {

	public DerpCommand(MinecraftBotWrapper bot) {
		super(bot, "derp", "wat");
	}

	@Override
	public void execute(String[] args) {
		DerpTask task = bot.getTaskManager().getTaskFor(DerpTask.class);
		if(!task.isActive())
			task.start(args);
		else
			task.stop();
	}
}
