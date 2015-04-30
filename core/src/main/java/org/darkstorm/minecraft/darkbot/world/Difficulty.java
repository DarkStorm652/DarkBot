package org.darkstorm.minecraft.darkbot.world;

public enum Difficulty {
	PEACEFUL(0),
	EASY(1),
	NORMAL(2),
	HARD(3);

	private final int id;

	private Difficulty(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static Difficulty getDifficultyById(int id) {
		for(Difficulty dimension : values())
			if(dimension.getId() == id)
				return dimension;
		return null;
	}
}
