package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class MuteCommand extends AbstractCommand {

	public MuteCommand(MinecraftBotWrapper bot) {
		super(bot, "mute", "Toggle chat muting", "[on/off]", "(?i)(on|off)?");
	}

	@Override
	public void execute(String[] args) {
		if(args.length == 1)
			bot.setMuted(args[0].equalsIgnoreCase("on"));
		else
			bot.setMuted(!bot.isMuted());
		controller.say(bot.isMuted() ? "Now muted." : "No longer muted.");
	}
}
