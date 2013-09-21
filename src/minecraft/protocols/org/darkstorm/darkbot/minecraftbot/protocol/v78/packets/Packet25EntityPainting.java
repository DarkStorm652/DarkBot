package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.block.ArtType;

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
