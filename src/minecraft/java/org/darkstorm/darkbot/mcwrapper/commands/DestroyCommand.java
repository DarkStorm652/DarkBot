package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.DestroyingTask;

public class DestroyCommand extends AbstractCommand {

	public DestroyCommand(MinecraftBotWrapper bot) {
		super(bot, "destroy", "Break all blocks in a cuboid area", "<x1> <y1> <z1> <x2> <y2> <z2>", "[=]?[-]?[0-9]+( [=]?[-]?[0-9]+){5}");
	}

	@Override
	public void execute(String[] args) {
		for(int i = 0; i < args.length; i++)
			if(args[i].startsWith("="))
				args[i] = args[i].substring(1);
		bot.getTaskManager().getTaskFor(DestroyingTask.class).start(args);
		controller.say("Now destroying!");
	}
}
