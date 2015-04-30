package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.TwerkTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class TwerkCommand extends AbstractCommand {

	public TwerkCommand(MinecraftBotWrapper bot) {
		super(bot, "twerk", "wat");
	}

	@Override
	public void execute(String[] args) {
		TwerkTask task = bot.getTaskManager().getTaskFor(TwerkTask.class);
		if(!task.isActive())
			task.start(args);
		else
			task.stop();
	}
}
