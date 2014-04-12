package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S0APacketEnterBed extends AbstractPacketX implements ReadablePacket {
	private int entityId, bedX, bedY, bedZ;

	public S0APacketEnterBed() {
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
