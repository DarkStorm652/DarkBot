package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class Packet5PlayerInventory extends AbstractPacket implements
		ReadablePacket {
	public int entityID;

	public int slot;
	public ItemStack item;

	public Packet5PlayerInventory() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityID = in.readInt();
		slot = in.readShort();
		item = readItemStack(in);
	}

	public int getId() {
		return 5;
	}
}
