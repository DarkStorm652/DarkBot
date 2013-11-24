package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class Packet102WindowClick extends AbstractPacket implements WriteablePacket {
	public int windowId;
	public int slot;
	public int button;
	public short action;
	public ItemStack item;
	public boolean shift;

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeShort(slot);
		out.writeByte(button);
		out.writeShort(action);
		out.writeByte(shift ? 1 : 0);
		writeItemStack(item, out);
	}

	@Override
	public int getId() {
		return 102;
	}
}
