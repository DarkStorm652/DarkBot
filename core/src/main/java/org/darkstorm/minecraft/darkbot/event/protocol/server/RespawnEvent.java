package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;
import org.darkstorm.minecraft.darkbot.world.*;

public class RespawnEvent extends ProtocolEvent {
	private final int respawnDimension;
	private final Difficulty difficulty;
	private final GameMode gameMode;
	private final WorldType worldType;
	private final int worldHeight;

	public RespawnEvent(int respawnDimension, Difficulty difficulty, GameMode gameMode, WorldType worldType, int worldHeight) {
		this.respawnDimension = respawnDimension;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.worldType = worldType;
		this.worldHeight = worldHeight;
	}

	public int getRespawnDimension() {
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
