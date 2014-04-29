package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS0A_EnterBed extends AbstractPacketX implements ReadablePacket {
	private int entityId, bedX, bedY, bedZ;

	public PacketS0A_EnterBed() {
		super(0x0A, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		bedX = in.readInt();
		bedY = in.read();
		bedZ = in.readInt();
	}

	public int getEntityId() {
		return entityId;
	}
	
	public int getBedX() {
		return bedX;
	}
	
	public int getBedY() {
		return bedY;
	}
	
	public int getBedZ() {
		return bedZ;
	}
}
