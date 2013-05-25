package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.DestroyingTask;

public class DestroyCommand extends AbstractCommand {

	public DestroyCommand(DarkBotMC bot) {
		super(bot, "destroy", "Break all blocks in a cuboid area",
				"<x1> <y1> <z1> <x2> <y2> <z2>", "=[-]?[0-9]+( =[-]?[0-9]+){5}");
	}

	@Override
	public void execute(String[] args) {
		for(int i = 0; i < args.length; i++)
			args[i] = args[i].split("=")[1];
		bot.getTaskManager().getTaskFor(DestroyingTask.class).start(args);
		controller.say("Now destroying!");
	}
}
