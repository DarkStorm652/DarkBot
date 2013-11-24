package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.DerpTask;

public class DerpCommand extends AbstractCommand {

	public DerpCommand(MinecraftBotWrapper bot) {
		super(bot, "derp", "wat");
	}

	@Override
	public void execute(String[] args) {
		bot.getTaskManager().getTaskFor(DerpTask.class).start(args);
	}
}
