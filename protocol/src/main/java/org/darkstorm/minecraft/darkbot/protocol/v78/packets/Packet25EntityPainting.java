package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.block.ArtType;

import java.io.*;

public class Packet25EntityPainting extends AbstractPacket implements
		ReadablePacket {
	public int entityId;
	public String title;

	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int direction;

	public Packet25EntityPainting() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		title = readString(in, ArtType.MAX_ART_TITLE_LENGTH);
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
		direction = in.readInt();
	}

	public int getId() {
		return 25;
	}
}
