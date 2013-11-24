package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

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
