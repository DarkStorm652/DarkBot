package org.darkstorm.darkbot.minecraftbot.schematic;

public enum SchematicMaterialVersion {
	CLASSIC("Classic"),
	CURRENT("Alpha");

	private final String name;

	private SchematicMaterialVersion(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static SchematicMaterialVersion getByName(String name) {
		for(SchematicMaterialVersion version : values())
			if(name.equalsIgnoreCase(version.getName()))
				return version;
		return null;
	}
}
