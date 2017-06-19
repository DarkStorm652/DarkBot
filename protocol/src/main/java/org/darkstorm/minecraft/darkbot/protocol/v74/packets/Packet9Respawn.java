package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.*;

import java.io.*;

public class Packet9Respawn extends AbstractPacket implements ReadablePacket {
	public Dimension respawnDimension;
	public Difficulty difficulty;
	public GameMode gameMode;
	public WorldType worldType;

	public int worldHeight;

	public Packet9Respawn() {
	}

	public void readData(DataInputStream in) throws IOException {
		respawnDimension = Dimension.getDimensionById(in.readInt());
		difficulty = Difficulty.getDifficultyById(in.readByte());
		gameMode = GameMode.getGameModeById(in.readByte());
		worldHeight = in.readShort();
		String s = readString(in, 16);
		worldType = WorldType.parseWorldType(s);

		if(worldType == null)
			worldType = WorldType.DEFAULT;
	}

	public int getId() {
		return 9;
	}
}
