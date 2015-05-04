package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class CrouchCommand extends AbstractCommand {

	public CrouchCommand(MinecraftBotWrapper bot) {
		super(bot, "crouch", "Toggle crouching", "[on/off]", "(?i)(on|off)?");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		if(args.length == 1)
			player.setCrouching(args[0].equalsIgnoreCase("on"));
		else
			player.setCrouching(!player.isCrouching());
		controller.say(player.isCrouching() ? "Now crouching." : "No longer crouching.");
	}
}
