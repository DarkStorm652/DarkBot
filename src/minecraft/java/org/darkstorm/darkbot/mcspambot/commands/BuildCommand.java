package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.BuildingTask;

public class BuildCommand extends AbstractCommand {

	public BuildCommand(DarkBotMC bot) {
		super(bot, "build", "Fill a cuboid area with blocks",
				"<id> <x1> <y1> <z1> <x2> <y2> <z2>",
				"=[0-9]+( =[-]?[0-9]+){6}");
	}

	@Override
	public void execute(String[] args) {
		for(int i = 0; i < args.length; i++)
			args[i] = args[i].split("=")[1];
		bot.getTaskManager().getTaskFor(BuildingTask.class).start(args);
		controller.say("Now building!");
	}
}
