package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.InventoryType;

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
