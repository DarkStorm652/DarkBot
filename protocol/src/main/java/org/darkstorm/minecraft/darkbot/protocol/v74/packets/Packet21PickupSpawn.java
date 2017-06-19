package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class Packet21PickupSpawn extends AbstractPacket implements
		ReadablePacket {
	public int entityId;

	public int xPosition;
	public int yPosition;
	public int zPosition;
	public byte rotation;
	public byte pitch;

	public byte roll;
	public ItemStack item;

	public Packet21PickupSpawn() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		item = readItemStack(in);
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
		rotation = in.readByte();
		pitch = in.readByte();
		roll = in.readByte();
	}

	public int getId() {
		return 21;
	}
}
