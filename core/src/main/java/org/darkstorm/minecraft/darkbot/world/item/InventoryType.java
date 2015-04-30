package org.darkstorm.minecraft.darkbot.world.item;

public enum InventoryType {
	CHEST,
	WORKBENCH,
	FURNACE,
	DISPENSER,
	ENCHANTMENT_TABLE,
	BREWING_STAND,
	NPC_TRADE,
	BEACON,
	ANVIL,
	HOPPER,
	UNKNOWN10,
	ANIMAL_CHEST;

	public static InventoryType byId(int id) {
		if(id < 0 || id >= values().length)
			return null;
		return values()[id];
	}
}
