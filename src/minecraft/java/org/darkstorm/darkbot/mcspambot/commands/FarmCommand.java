package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.FarmingTask;

public class FarmCommand extends AbstractCommand {

	public FarmCommand(MinecraftBotWrapper bot) {
		super(bot, "farm", "Activate the farming task", "[<x1> <y1> <z1> <x2> <y2> <z2>]", "([=]?[-]?[0-9]+( [=]?[-]?[0-9]+){5})?");
	}

	@Override
	public void execute(String[] args) {
		FarmingTask task = bot.getTaskManager().getTaskFor(FarmingTask.class);
		for(int i = 0; i < args.length; i++)
			if(args[i].startsWith("="))
				args[i] = args[i].substring(1);
		task.start(args);
		controller.say("Now farming!");
	}
}
