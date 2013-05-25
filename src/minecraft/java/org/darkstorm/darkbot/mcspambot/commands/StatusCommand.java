package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class StatusCommand extends AbstractCommand {

	public StatusCommand(DarkBotMC bot) {
		super(bot, "status", "Display bot state information");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;

		controller.say("Health: [" + player.getHealth() + "/20] Hunger: ["
				+ player.getHunger() + "/20] Level "
				+ player.getExperienceLevel() + " ("
				+ player.getExperienceTotal() + " total exp.)");
		try {
			String tasks = "";
			String activeTasks = "";
			TaskManager manager = bot.getTaskManager();
			for(Task task : manager.getRegisteredTasks()) {
				tasks += task.getClass().getSimpleName() + ", ";
				if(task.isActive())
					activeTasks += task.getClass().getSimpleName() + ", ";
			}
			if(!tasks.isEmpty())
				tasks = tasks.substring(0, tasks.length() - 2);
			if(!activeTasks.isEmpty())
				activeTasks = activeTasks
						.substring(0, activeTasks.length() - 2);
			bot.say("Tasks: [" + tasks + "] Active: [" + activeTasks + "]");
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}
