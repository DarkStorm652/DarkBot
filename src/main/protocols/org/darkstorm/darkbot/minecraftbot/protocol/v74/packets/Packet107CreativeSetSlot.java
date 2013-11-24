package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class Packet107CreativeSetSlot extends AbstractPacket implements
		ReadablePacket, WriteablePacket {
	public int slot;
	public ItemStack itemStack;

	public Packet107CreativeSetSlot() {
	}

	public Packet107CreativeSetSlot(int par1, ItemStack par2ItemStack) {
		slot = par1;
		itemStack = par2ItemStack;
	}

	public void readData(DataInputStream in) throws IOException {
		slot = in.readShort();
		itemStack = readItemStack(in);
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeShort(slot);
		writeItemStack(itemStack, out);
	}

	public int getId() {
		return 107;
	}
}
