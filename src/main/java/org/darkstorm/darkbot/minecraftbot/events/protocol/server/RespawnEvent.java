package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;
import org.darkstorm.darkbot.minecraftbot.world.*;

public class RespawnEvent extends ProtocolEvent {
	private final Dimension respawnDimension;
	private final Difficulty difficulty;
	private final GameMode gameMode;
	private final WorldType worldType;
	private final int worldHeight;

	public RespawnEvent(Dimension respawnDimension, Difficulty difficulty, GameMode gameMode, WorldType worldType, int worldHeight) {
		this.respawnDimension = respawnDimension;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.worldType = worldType;
		this.worldHeight = worldHeight;
	}

	public Dimension getRespawnDimension() {
		return respawnDimension;
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

	public int getWorldHeight() {
		return worldHeight;
	}
}
