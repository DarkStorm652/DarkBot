package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class Packet103SetSlot extends AbstractPacket implements ReadablePacket {
	public int windowId;
	public int itemSlot;
	public ItemStack itemStack;

	public Packet103SetSlot() {
	}

	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		itemSlot = in.readShort();
		itemStack = readItemStack(in);
	}

	public int getId() {
		return 103;
	}
}
