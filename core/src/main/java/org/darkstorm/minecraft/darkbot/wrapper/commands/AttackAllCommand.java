package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.HostileTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class AttackAllCommand extends AbstractCommand {

	public AttackAllCommand(MinecraftBotWrapper bot) {
		super(bot, "attackall", "Attack all nearby players and monsters");
	}

	@Override
	public void execute(String[] args) {
		HostileTask task = bot.getTaskManager().getTaskFor(HostileTask.class);
		if(task.isActive()) {
			task.stop();
			controller.say("No longer in hostile mode.");
		} else {
			task.start();
			controller.say("Now in hostile mode!");
		}
	}
}
