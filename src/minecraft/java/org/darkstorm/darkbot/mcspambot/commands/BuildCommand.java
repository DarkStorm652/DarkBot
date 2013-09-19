package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.BuildingTask;

public class BuildCommand extends AbstractCommand {

	public BuildCommand(MinecraftBotWrapper bot) {
		super(bot, "build", "Fill a cuboid area with blocks", "<id> <x1> <y1> <z1> <x2> <y2> <z2>", "[=]?[0-9]+( [=]?[-]?[0-9]+){6}");
	}

	@Override
	public void execute(String[] args) {
		for(int i = 0; i < args.length; i++)
			if(args[i].startsWith("="))
				args[i] = args[i].substring(1);
		bot.getTaskManager().getTaskFor(BuildingTask.class).start(args);
		controller.say("Now building!");
	}
}
