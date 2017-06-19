package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.*;

import java.io.*;

public class Packet1Login extends AbstractPacket implements ReadablePacket {
	public int playerId;
	public WorldType worldType = WorldType.DEFAULT;
	public boolean bool;

	public GameMode gameMode = GameMode.SURVIVAL;
	public Dimension dimension = Dimension.OVERWORLD;
	public Difficulty difficulty = Difficulty.EASY;

	public byte worldHeight, maxPlayers;

	public Packet1Login() {
	}

	public Packet1Login(int id) {
		playerId = id;
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		playerId = in.readInt();
		String s = readString(in, 16);
		worldType = WorldType.parseWorldType(s);

		if(worldType == null) {
			worldType = WorldType.DEFAULT;
		}

		byte b = in.readByte();
		bool = (b & 8) == 8;

		gameMode = GameMode.getGameModeById(b & -9);
		dimension = Dimension.getDimensionById(in.readByte());
		difficulty = Difficulty.getDifficultyById(in.readByte());
		worldHeight = in.readByte();
		maxPlayers = in.readByte();
	}

	@Override
	public int getId() {
		return 1;
	}
}
