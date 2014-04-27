package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS33_SignUpdate extends AbstractPacketX implements ReadablePacket {
	private int x, y, z;
	private String[] lines;

	public PacketS33_SignUpdate() {
		super(0x33, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readShort();
		z = in.readInt();

		lines = new String[4];
		for(int i = 0; i < 4; i++)
			lines[i] = readString(in);
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

	public String[] getLines() {
		return lines.clone();
	}
}
