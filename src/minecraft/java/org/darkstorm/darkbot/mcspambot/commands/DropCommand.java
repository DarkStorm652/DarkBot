package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public class DropCommand extends AbstractCommand {

	public DropCommand(DarkBotMC bot) {
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
