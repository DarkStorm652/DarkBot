package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet23VehicleSpawn extends AbstractPacket implements
		ReadablePacket {
	public int entityId;

	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int speedX;
	public int speedY;
	public int speedZ;
	public int field_92077_h;
	public int field_92078_i;

	public int type;
	public int throwerEntityId;

	public Packet23VehicleSpawn() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		type = in.readByte();
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
		field_92077_h = in.readByte();
		field_92078_i = in.readByte();
		throwerEntityId = in.readInt();

		if(throwerEntityId > 0) {
			speedX = in.readShort();
			speedY = in.readShort();
			speedZ = in.readShort();
		}
	}

	public int getId() {
		return 23;
	}
}
