package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

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
