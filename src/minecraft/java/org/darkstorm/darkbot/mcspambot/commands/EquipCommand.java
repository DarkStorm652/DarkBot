package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class EquipCommand extends AbstractCommand {

	public EquipCommand(DarkBotMC bot) {
		super(bot, "equip", "Equip armor in inventory or remove current armor");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		PlayerInventory inventory = player.getInventory();
		boolean helmet = inventory.getArmorAt(0) != null;
		boolean chestplate = inventory.getArmorAt(1) != null;
		boolean leggings = inventory.getArmorAt(2) != null;
		boolean boots = inventory.getArmorAt(3) != null;
		boolean changed = false;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item == null)
				continue;
			int armorSlot;
			int id = item.getId();
			if(!helmet
					&& (id == 86 || id == 298 || id == 302 || id == 306
							|| id == 310 || id == 314)) {
				armorSlot = 0;
				helmet = true;
			} else if(!chestplate
					&& (id == 299 || id == 303 || id == 307 || id == 311 || id == 315)) {
				armorSlot = 1;
				chestplate = true;
			} else if(!leggings
					&& (id == 300 || id == 304 || id == 308 || id == 312 || id == 316)) {
				armorSlot = 2;
				leggings = true;
			} else if(!boots
					&& (id == 301 || id == 305 || id == 309 || id == 313 || id == 317)) {
				armorSlot = 3;
				boots = true;
			} else if(helmet && chestplate && leggings && boots)
				break;
			else
				continue;
			inventory.selectItemAt(i);
			inventory.selectArmorAt(armorSlot);
			changed = true;
		}
		if(!changed) {
			for(int i = 0; i < 36; i++) {
				ItemStack item = inventory.getItemAt(i);
				if(item != null)
					continue;
				int armorSlot;
				if(helmet) {
					armorSlot = 0;
					helmet = false;
				} else if(chestplate) {
					armorSlot = 1;
					chestplate = false;
				} else if(leggings) {
					armorSlot = 2;
					leggings = false;
				} else if(boots) {
					armorSlot = 3;
					boots = false;
				} else if(!helmet && !chestplate && !leggings && !boots)
					break;
				else
					continue;
				inventory.selectArmorAt(armorSlot);
				inventory.selectItemAt(i);
			}
		}
		inventory.close();
		if(changed)
			controller.say("Equipped armor.");
		else
			controller.say("Removed armor.");
	}
}
