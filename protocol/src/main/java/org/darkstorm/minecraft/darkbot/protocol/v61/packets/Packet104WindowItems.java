package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class Packet104WindowItems extends AbstractPacket implements ReadablePacket {
	public int windowId;
	public ItemStack[] itemStack;

	public Packet104WindowItems() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		short count = in.readShort();
		itemStack = new ItemStack[count];

		for(int i = 0; i < count; i++)
			itemStack[i] = readItemStack(in);
	}

	@Override
	public int getId() {
		return 104;
	}
}
