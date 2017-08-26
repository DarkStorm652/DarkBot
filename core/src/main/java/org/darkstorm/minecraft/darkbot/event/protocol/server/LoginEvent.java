package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class LoginEvent extends ProtocolEvent {
	private final int playerId;
	private final WorldType worldType;
	private final GameMode gameMode;
	private final int dimension;
	private final Difficulty difficulty;
	private final int worldHeight, maxPlayers;

	public LoginEvent(int playerId, WorldType worldType, GameMode gameMode, int dimension, Difficulty difficulty, int worldHeight, int maxPlayers) {
		this.playerId = playerId;
		this.worldType = worldType;
		this.gameMode = gameMode;
		this.dimension = dimension;
		this.difficulty = difficulty;
		this.worldHeight = worldHeight;
		this.maxPlayers = maxPlayers;
	}

	public int getPlayerId() {
		return playerId;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public int getDimension() {
		return dimension;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}
}
