package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;
import org.darkstorm.darkbot.minecraftbot.world.*;

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
