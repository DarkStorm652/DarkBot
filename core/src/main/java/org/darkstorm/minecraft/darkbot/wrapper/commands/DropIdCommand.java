package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.*;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class DropIdCommand extends AbstractCommand {

	public DropIdCommand(MinecraftBotWrapper bot) {
		super(bot, "dropid", "Drop all items of a specific ID", "<id>",
				"[0-9]+");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		PlayerInventory inventory = player.getInventory();
		int id = Integer.parseInt(args[0]);
		for(int slot = 0; slot < 40; slot++) {
			ItemStack item = inventory.getItemAt(slot);
			if(item != null && item.getId() == id) {
				inventory.selectItemAt(slot, true);
				inventory.dropSelectedItem();
			}
		}
		inventory.close();
		controller.say("Dropped all items of ID " + id + "!");
	}
}
