package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;

public class OwnerCommand extends AbstractCommand {

	public OwnerCommand(DarkBotMC bot) {
		super(bot, "owner", "Set the bot owner", "<owner>", "[\\w]{1,16}");
	}

	@Override
	public void execute(String[] args) {
		controller.setOwner(args[0]);
		controller.say("Set owner to " + args[0] + ".");
	}
}
