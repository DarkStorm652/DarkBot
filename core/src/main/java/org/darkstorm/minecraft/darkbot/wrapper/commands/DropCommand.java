package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.PlayerInventory;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class DropCommand extends AbstractCommand {

	public DropCommand(MinecraftBotWrapper bot) {
		super(bot, "drop", "Drop an item in a slot", "<slot>", "[0-9]+");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		PlayerInventory inventory = player.getInventory();
		int slot = Integer.parseInt(args[0]);
		if(slot < 0 || slot >= 40) {
			controller.say("Invalid slot.");
			return;
		}
		if(inventory.getItemAt(slot) != null) {
			inventory.selectItemAt(slot, true);
			inventory.dropSelectedItem();
		}
	}
}
