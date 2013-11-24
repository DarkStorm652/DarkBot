package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public class SwitchCommand extends AbstractCommand {

	public SwitchCommand(MinecraftBotWrapper bot) {
		super(bot, "switch", "Move items in inventory",
				"<slot1 0-45> <slot2 0-45>",
				"([1-3]?[0-9]|4[0-5]) ([1-3]?[0-9]|4[0-5])");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		PlayerInventory inventory = player.getInventory();
		int slot1 = Integer.parseInt(args[0]);
		int slot2 = Integer.parseInt(args[1]);
		inventory.selectItemAt(slot1);
		inventory.selectItemAt(slot2);
		inventory.selectItemAt(slot1);
		inventory.close();
	}
}
