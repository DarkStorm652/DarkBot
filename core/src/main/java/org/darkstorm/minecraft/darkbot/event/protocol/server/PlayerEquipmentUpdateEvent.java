package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

public class PlayerEquipmentUpdateEvent extends EntityEvent {
	private final EquipmentSlot slot;
	private final ItemStack item;

	public PlayerEquipmentUpdateEvent(int playerId, int slot, ItemStack item) {
		this(playerId, EquipmentSlot.fromId(slot), item);
	}

	public PlayerEquipmentUpdateEvent(int playerId, EquipmentSlot slot, ItemStack item) {
		super(playerId);

		this.slot = slot;
		this.item = item;
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}

	public enum EquipmentSlot {
		HELD(0),
		HELMET(1),
		CHESTPLATE(2),
		LEGGINGS(3),
		BOOTS(4);

		private final int id;

		private EquipmentSlot(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static EquipmentSlot fromId(int id) {
			for(EquipmentSlot slot : values())
				if(id == slot.id)
					return slot;
			return null;
		}
	}
}
