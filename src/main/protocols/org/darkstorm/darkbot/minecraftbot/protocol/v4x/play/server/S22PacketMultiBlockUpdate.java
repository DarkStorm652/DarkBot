package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S22PacketMultiBlockUpdate extends AbstractPacketX implements ReadablePacket {
	private int x, z;
	private int[] blockData;

	public S22PacketMultiBlockUpdate() {
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
