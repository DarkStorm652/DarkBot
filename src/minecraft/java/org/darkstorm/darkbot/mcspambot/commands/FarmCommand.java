package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.ai.FarmingTask.StorageAction;

public class FarmCommand extends AbstractCommand {

	public FarmCommand(MinecraftBotWrapper bot) {
		super(bot, "farm", "Activate the farming task", "[<x1> <y1> <z1> <x2> <y2> <z2>] [STORE|sell]", "(?i)([=]?[-]?[0-9]+( [=]?[-]?[0-9]+){5})?( (sell|store))?");
	}

	@Override
	public void execute(String[] args) {
		FarmingTask task = bot.getTaskManager().getTaskFor(FarmingTask.class);
		int length = args.length;
		if(args.length > 0 && (args[args.length - 1].equalsIgnoreCase("store") || args[args.length - 1].equalsIgnoreCase("sell"))) {
			task.setStorageAction(args[args.length - 1].equalsIgnoreCase("store") ? StorageAction.STORE : StorageAction.SELL);
			length--;
		}
		for(int i = 0; i < length; i++)
			if(args[i].startsWith("="))
				args[i] = args[i].substring(1);
		task.start(args);
		controller.say("/r " + "Now farming!");
	}
}
