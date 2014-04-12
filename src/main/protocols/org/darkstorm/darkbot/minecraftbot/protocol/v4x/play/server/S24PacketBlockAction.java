package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S24PacketBlockAction extends AbstractPacketX implements ReadablePacket {
	private int x, y, z;
	private int blockId, data1, data2;

	public S24PacketBlockAction() {
		super(0x24, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readShort();
		z = in.readInt();

		data1 = in.read();
		data2 = in.read();
		blockId = readVarInt(in);
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

	public int getBlockId() {
		return blockId;
	}

	public int getData1() {
		return data1;
	}

	public int getData2() {
		return data2;
	}
}
