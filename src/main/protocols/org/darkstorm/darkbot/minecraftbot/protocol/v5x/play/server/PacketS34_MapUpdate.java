package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS34_MapUpdate extends AbstractPacketX implements ReadablePacket {
	private int mapId;
	private byte[] data;

	public PacketS34_MapUpdate() {
		super(0x34, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		mapId = readVarInt(in);
		data = readByteArray(in);
	}

	public int getMapId() {
		return mapId;
	}

	public byte[] getData() {
		return data;
	}
}
