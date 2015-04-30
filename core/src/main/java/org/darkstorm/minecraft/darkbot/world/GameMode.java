package org.darkstorm.minecraft.darkbot.world;

public enum GameMode {
	NONE(-1, ""),
	SURVIVAL(0, "survival"),
	CREATIVE(1, "creative"),
	ADVENTURE(2, "adventure");

	private final String name;
	private final int id;

	private GameMode(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public static GameMode getGameModeById(int id) {
		for(GameMode gameMode : values())
			if(gameMode.getId() == id)
				return gameMode;
		return null;
	}
}
