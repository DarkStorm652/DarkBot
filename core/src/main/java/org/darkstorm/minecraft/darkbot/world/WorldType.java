package org.darkstorm.darkbot.minecraftbot.world;

public enum WorldType {
	DEFAULT("default"),
	FLAT("flat"),
	DEFAULT_1_1("default_1_1"),
	LARGE_BIOMES("largeBiomes"),
	AMPLIFIED("amplified");

	private final String name;

	private WorldType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static WorldType parseWorldType(String name) {
		for(WorldType worldType : values())
			if(worldType.getName().equals(name))
				return worldType;
		return null;
	}
}