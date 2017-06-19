package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;
import org.darkstorm.minecraft.darkbot.world.*;

import java.io.*;

public class PacketS07_Respawn extends AbstractPacketX implements ReadablePacket {
	private Dimension dimension;
	private Difficulty difficulty;
	private GameMode gameMode;
	private WorldType worldType;

	public PacketS07_Respawn() {
		super(0x07, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		dimension = Dimension.getDimensionById(in.readInt());
		difficulty = Difficulty.getDifficultyById(in.read());
		gameMode = GameMode.getGameModeById(in.read());
		worldType = WorldType.parseWorldType(readString(in));
	}

	public Dimension getDimension() {
		return dimension;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public WorldType getWorldType() {
		return worldType;
	}
}
