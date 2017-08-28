package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

public class PlayerEquipmentUpdateEvent extends EntityEvent {
	private final EquipmentSlot slot;
	private final ItemStack item;

	public PlayerEquipmentUpdateEvent(int playerId, int slot, ItemStack item) {
		this(playerId, EquipmentSlot.values()[slot], item);
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
}
