package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.PlayerInventory;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class DropAllCommand extends AbstractCommand {

	public DropAllCommand(MinecraftBotWrapper bot) {
		super(bot, "dropall", "Drop all items in inventory");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		PlayerInventory inventory = player.getInventory();
		for(int slot = 0; slot < 40; slot++) {
			if(inventory.getItemAt(slot) != null) {
				inventory.selectItemAt(slot, true);
				inventory.dropSelectedItem();
			}
		}
		inventory.close();
		controller.say("Dropped all items!");
	}
}
