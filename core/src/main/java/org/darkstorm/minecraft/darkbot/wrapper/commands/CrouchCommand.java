package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

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
