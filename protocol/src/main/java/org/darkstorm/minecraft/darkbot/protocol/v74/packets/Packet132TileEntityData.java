package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.nbt.NBTTagCompound;
import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet132TileEntityData extends AbstractPacket implements ReadablePacket {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int actionType;
	public NBTTagCompound compound;

	public Packet132TileEntityData() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		xPosition = in.readInt();
		yPosition = in.readShort();
		zPosition = in.readInt();
		actionType = in.readByte();
		compound = readNBTTagCompound(in);
	}

	@Override
	public int getId() {
		return 132;
	}
}
