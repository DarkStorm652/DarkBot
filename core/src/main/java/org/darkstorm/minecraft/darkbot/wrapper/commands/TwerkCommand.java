package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.TwerkTask;

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
