package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS23_BlockUpdate extends AbstractPacketX implements ReadablePacket {
	private int x, y, z;
	private int blockId, blockMetadata;

	public PacketS23_BlockUpdate() {
		super(0x23, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.read();
		z = in.readInt();

		blockId = readVarInt(in);
		blockMetadata = in.read();
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

	public int getBlockMetadata() {
		return blockMetadata;
	}
}
