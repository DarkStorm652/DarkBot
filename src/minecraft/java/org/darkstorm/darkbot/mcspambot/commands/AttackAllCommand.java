package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.HostileTask;

public class AttackAllCommand extends AbstractCommand {

	public AttackAllCommand(MinecraftBotWrapper bot) {
		super(bot, "attackall", "Attack all nearby players and monsters");
	}

	@Override
	public void execute(String[] args) {
		HostileTask task = bot.getTaskManager().getTaskFor(HostileTask.class);
		if(task.isActive()) {
			task.stop();
			controller.say("/r " + "No longer in hostile mode.");
		} else {
			task.start();
			controller.say("/r " + "Now in hostile mode!");
		}
	}
}
