package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.item.InventoryType;

import java.io.*;

public class Packet100OpenWindow extends AbstractPacket implements ReadablePacket {
	public int windowId;
	public InventoryType inventoryType;
	public String windowTitle;
	public int slotsCount;
	public boolean flag;

	public Packet100OpenWindow() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte() & 255;
		inventoryType = InventoryType.byId(in.readByte() & 255);
		windowTitle = readString(in, 32);
		slotsCount = in.readByte() & 255;
		flag = in.readBoolean();
	}

	@Override
	public int getId() {
		return 100;
	}
}
