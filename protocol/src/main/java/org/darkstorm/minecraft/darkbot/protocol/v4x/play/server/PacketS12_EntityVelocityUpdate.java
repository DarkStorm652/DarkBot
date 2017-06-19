package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS12_EntityVelocityUpdate extends AbstractPacketX implements ReadablePacket {
	private int entityId;
	private double velocityX, velocityY, velocityZ;

	public PacketS12_EntityVelocityUpdate() {
		super(0x12, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();

		velocityX = in.readShort() / 8000D;
		velocityY = in.readShort() / 8000D;
		velocityZ = in.readShort() / 8000D;
	}

	public int getEntityId() {
		return entityId;
	}

	public double getVelocityX() {
		return velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}

	public double getVelocityZ() {
		return velocityZ;
	}
}
