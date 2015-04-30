package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class CrouchCommand extends AbstractCommand {

	public CrouchCommand(MinecraftBotWrapper bot) {
		super(bot, "crouch", "Toggle crouching");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		player.setCrouching(!player.isCrouching());
		controller.say(player.isCrouching() ? "Now crouching." : "No longer crouching.");
	}
}
