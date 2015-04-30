package org.darkstorm.minecraft.darkbot.world.block;

public enum BiomeType {
	UNKNOWN(-1),
	OCEAN(0),
	PLAINS(1),
	DESERT(2),
	EXTREME_HILLS(3),
	FOREST(4),
	TAIGA(5),
	SWAMPLAND(6),
	RIVER(7),
	HELL(8),
	SKY(9),
	FROZEN_OCEAN(10),
	FROZEN_RIVER(11),
	ICE_PLAINS(12),
	ICE_MOUNTAINS(13),
	MUSHROOM_ISLAND(14),
	MUSHROOM_ISLAND_SHORE(15),
	BEACH(16),
	DESERT_HILLS(17),
	FOREST_HILLS(18),
	TAIGA_HILLS(19),
	EXTREME_HILLS_EDGE(20),
	JUNGLE(21),
	JUNGLE_HILLS(22);

	private final int id;

	private BiomeType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static BiomeType getById(int id) {
		for(BiomeType type : BiomeType.values())
			if(id == type.id)
				return type;
		return UNKNOWN;
	}
}
