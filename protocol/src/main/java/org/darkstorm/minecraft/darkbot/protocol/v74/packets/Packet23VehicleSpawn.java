package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet23VehicleSpawn extends AbstractPacket implements ReadablePacket {
	public int entityId;

	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int speedX;
	public int speedY;
	public int speedZ;
	public int yaw;
	public int pitch;

	public int type;
	public int throwerEntityId;

	public Packet23VehicleSpawn() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		type = in.readByte();
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
		yaw = in.readByte();
		pitch = in.readByte();
		throwerEntityId = in.readInt();

		if(throwerEntityId > 0) {
			speedX = in.readShort();
			speedY = in.readShort();
			speedZ = in.readShort();
		}
	}

	@Override
	public int getId() {
		return 23;
	}
}
