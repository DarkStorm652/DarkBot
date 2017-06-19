package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet108EnchantItem extends AbstractPacket implements WriteablePacket {
	public int windowId;
	public int enchantment;

	public Packet108EnchantItem() {
	}

	public Packet108EnchantItem(int par1, int par2) {
		windowId = par1;
		enchantment = par2;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeByte(enchantment);
	}

	@Override
	public int getId() {
		return 108;
	}
}
