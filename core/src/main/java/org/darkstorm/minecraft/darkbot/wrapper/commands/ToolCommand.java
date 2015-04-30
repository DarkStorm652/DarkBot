package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.PlayerInventory;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

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
