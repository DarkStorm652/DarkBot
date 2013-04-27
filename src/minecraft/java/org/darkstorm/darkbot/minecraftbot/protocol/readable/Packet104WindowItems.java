package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class Packet104WindowItems extends AbstractPacket implements
		ReadablePacket {
	public int windowId;
	public ItemStack[] itemStack;

	public Packet104WindowItems() {
	}

	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		short word0 = in.readShort();
		itemStack = new ItemStack[word0];

		for(int i = 0; i < word0; i++) {
			itemStack[i] = readItemStack(in);
		}
	}

	public int getId() {
		return 104;
	}
}
