package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;

public class ChatDelayCommand extends AbstractCommand {

	public ChatDelayCommand(DarkBotMC bot) {
		super(bot, "chatdelay", "Change chat delay", "<delay>", "[0-9]+");
	}

	@Override
	public void execute(String[] args) {
		controller.say("Set chat delay!");
		bot.setMessageDelay(Integer.parseInt(args[0]));
	}
}
