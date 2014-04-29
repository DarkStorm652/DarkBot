package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class PacketS04_EntityEquipment extends AbstractPacketX implements ReadablePacket {
	private int entityId;
	private EquipmentSlot slot;
	private ItemStack item;

	public PacketS04_EntityEquipment() {
		super(0x04, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		slot = EquipmentSlot.values()[in.readShort()];
		item = readItemStack(in);
	}

	public int getEntityId() {
		return entityId;
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}

	public enum EquipmentSlot {
		HELD,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS
	}
}
