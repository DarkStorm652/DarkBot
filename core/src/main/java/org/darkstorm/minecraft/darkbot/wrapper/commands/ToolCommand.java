package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public class ToolCommand extends AbstractCommand {

	public ToolCommand(MinecraftBotWrapper bot) {
		super(bot, "tool", "Change held items", "<slot 0-8>", "[0-8]");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		PlayerInventory inventory = player.getInventory();
		inventory.setCurrentHeldSlot(Integer.parseInt(args[0]));
	}
}
