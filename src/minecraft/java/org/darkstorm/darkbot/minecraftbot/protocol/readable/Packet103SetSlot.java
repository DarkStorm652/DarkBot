package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

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
