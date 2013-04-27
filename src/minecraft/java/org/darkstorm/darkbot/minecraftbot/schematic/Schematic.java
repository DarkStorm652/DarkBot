package org.darkstorm.darkbot.minecraftbot.schematic;

import java.util.*;

import org.darkstorm.darkbot.minecraftbot.nbt.NBTTagCompound;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.Entity;

public class Schematic {
	private final int width, height, length;
	private final SchematicMaterialVersion materialVersion;
	private final byte[][][] blocks, data;
	private final List<Entity> entities;
	private final List<TileEntity> tileEntities;

	// WorldEdit only
	private final BlockLocation origin, offset;

	public Schematic(NBTTagCompound compound) {
		width = compound.getShort("Width");
		height = compound.getShort("Height");
		length = compound.getShort("Length");

		if(compound.hasKey("WEOriginX") && compound.hasKey("WEOriginY")
				&& compound.hasKey("WEOriginZ"))
			origin = new BlockLocation(compound.getInteger("WEOriginX"),
					compound.getInteger("WEOriginY"),
					compound.getInteger("WEOriginZ"));
		else
			origin = null;

		if(compound.hasKey("WEOffsetX") && compound.hasKey("WEOffsetY")
				&& compound.hasKey("WEOffsetZ"))
			offset = new BlockLocation(compound.getInteger("WEOffsetX"),
					compound.getInteger("WEOffsetY"),
					compound.getInteger("WEOffsetZ"));
		else
			offset = null;

		materialVersion = SchematicMaterialVersion.getByName(compound
				.getString("Materials"));

		byte[] rawBlocks = compound.getByteArray("Blocks");
		byte[] rawData = compound.getByteArray("Data");

		blocks = new byte[width][height][length];
		data = new byte[width][height][length];

		for(int y = 0; y < height; y++) {
			for(int z = 0; z < length; z++) {
				for(int x = 0; x < width; x++) {
					int index = y * width * length + z * width + x;
					blocks[x][y][z] = rawBlocks[index];
					data[x][y][z] = rawData[index];
				}
			}
		}

		List<Entity> entities = new ArrayList<Entity>();
		List<TileEntity> tileEntities = new ArrayList<TileEntity>();

		this.entities = Collections.unmodifiableList(entities);
		this.tileEntities = Collections.unmodifiableList(tileEntities);
	}

	public int getWidth() {
		return width;
	}

	public int getLength() {
		return length;
	}

	public int getHeight() {
		return height;
	}

	public BlockLocation getOrigin() {
		return origin;
	}

	public BlockLocation getOffset() {
		return offset;
	}

	public SchematicMaterialVersion getMaterialVersion() {
		return materialVersion;
	}

	public int getBlockIdAt(BlockLocation location) {
		return getBlockIdAt(location.getX(), location.getY(), location.getZ());
	}

	public int getBlockIdAt(int x, int y, int z) {
		if(x < 0 || x > width || y < 0 || y > length || z < 0 || z > height)
			return 0;
		return blocks[x][y][z];
	}

	public int getBlockMetadataAt(BlockLocation location) {
		return getBlockIdAt(location.getX(), location.getY(), location.getZ());
	}

	public int getBlockMetadataAt(int x, int y, int z) {
		if(x < 0 || x > width || y < 0 || y > length || z < 0 || z > height)
			return 0;
		return data[x][y][z];
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public List<TileEntity> getTileEntities() {
		return tileEntities;
	}
}
