package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS22_MultiBlockUpdate extends AbstractPacketX implements ReadablePacket {
	private int x, z;
	private int[] blockData;

	public PacketS22_MultiBlockUpdate() {
		super(0x22, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		z = in.readInt();

		blockData = new int[in.readShort()];
		in.readInt();
		for(int i = 0; i < blockData.length; i++)
			blockData[i] = in.readInt();
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public int[] getBlockData() {
		return blockData;
	}
}
