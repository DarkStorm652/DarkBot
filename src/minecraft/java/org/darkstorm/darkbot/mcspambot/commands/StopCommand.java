package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;

public class StopCommand extends AbstractCommand {

	public StopCommand(DarkBotMC bot) {
		super(bot, "stop", "Stop all tasks and activities");
	}

	@Override
	public void execute(String[] args) {
		bot.getTaskManager().stopAll();
		bot.setActivity(null);
		controller.say("Stopped all tasks.");
	}
}
