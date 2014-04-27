package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.nbt.NBTTagCompound;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS35_TileEntityUpdate extends AbstractPacketX implements ReadablePacket {
	private int x, y, z, action;
	private NBTTagCompound data;

	public PacketS35_TileEntityUpdate() {
		super(0x35, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readShort();
		z = in.readInt();

		action = in.read();
		data = readNBTTagCompound(in);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getAction() {
		return action;
	}

	public NBTTagCompound getData() {
		return data;
	}
}
