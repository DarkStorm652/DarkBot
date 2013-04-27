package org.darkstorm.darkbot.minecraftbot.protocol.writeable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class Packet102WindowClick extends AbstractPacket implements
		WriteablePacket {
	public int windowId;
	public int inventorySlot;
	public int mouseClick;
	public short action;
	public ItemStack itemStack;
	public boolean holdingShift;

	public Packet102WindowClick(int par1, int par2, int par3, boolean par4,
			ItemStack par5ItemStack, short par6) {
		windowId = par1;
		inventorySlot = par2;
		mouseClick = par3;
		itemStack = par5ItemStack;
		action = par6;
		holdingShift = par4;
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeShort(inventorySlot);
		out.writeByte(mouseClick);
		out.writeShort(action);
		out.writeByte(holdingShift ? 1 : 0);
		writeItemStack(itemStack, out);
	}

	@Override
	public int getId() {
		return 102;
	}
}
