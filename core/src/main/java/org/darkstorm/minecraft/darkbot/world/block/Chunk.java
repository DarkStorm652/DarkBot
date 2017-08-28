package org.darkstorm.minecraft.darkbot.world.block;

import java.util.*;

import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import org.darkstorm.minecraft.darkbot.event.EventBus;
import org.darkstorm.minecraft.darkbot.event.world.BlockChangeEvent;
import org.darkstorm.minecraft.darkbot.world.World;

public final class Chunk {
	private final World world;
	private final ChunkLocation location;
	private final BlockLocation baseLocation;
	private final BlockStorage blocks;
	private final NibbleArray3d light, skylight;
	private final byte[] biomes;
	private final Map<BlockLocation, TileEntity> tileEntities;

	public Chunk(World world, ChunkLocation location, BlockStorage blocks, NibbleArray3d light, NibbleArray3d skylight, byte[] biomes) {
		this.world = world;
		this.location = location;
		this.baseLocation = new BlockLocation(location);
		this.blocks = blocks;
		this.light = light;
		this.skylight = skylight;
		this.biomes = biomes;
		tileEntities = new HashMap<BlockLocation, TileEntity>();
	}

	public World getWorld() {
		return world;
	}

	public ChunkLocation getLocation() {
		return location;
	}
	
	public BlockLocation getBlockBaseLocation() {
		return baseLocation;
	}

	public TileEntity getTileEntityAt(int x, int y, int z) {
		return getTileEntityAt(new BlockLocation(x, y, z));
	}

	public TileEntity getTileEntityAt(BlockLocation location) {
		synchronized(tileEntities) {
			return tileEntities.get(location);
		}
	}

	public void setTileEntityAt(TileEntity tileEntity, int x, int y, int z) {
		setTileEntityAt(tileEntity, new BlockLocation(x, y, z));
	}

	public void setTileEntityAt(TileEntity tileEntity, BlockLocation location) {
		synchronized(tileEntities) {
			tileEntities.put(location, tileEntity);
		}
	}
	
	public Block getBlockAt(BlockLocation location) {
		return getBlockAt(location.getX(), location.getY(), location.getZ());
	}
	
	public Block getBlockAt(int x, int y, int z) {
		int id = getBlockIdAt(x, y, z);
		if(id <= 0)
			return null;
		int metadata = getBlockMetadataAt(x, y, z);
		
		BlockType type = BlockType.getById(id);
		return type.getBlockFactory().createBlock(world, this, baseLocation.offset(x, y, z), metadata);
	}

	public int getBlockIdAt(BlockLocation location) {
		return getBlockIdAt(location.getX(), location.getY(), location.getZ());
	}

	public int getBlockIdAt(int x, int y, int z) {
		BlockState block = blocks.get(x, y, z);
		if(block == null)
			return -1;
		return block.getId();
	}

	public void setBlockIdAt(int id, BlockLocation location) {
		setBlockIdAt(id, location.getX(), location.getY(), location.getZ());
	}

	public void setBlockIdAt(int id, int x, int y, int z) {
		BlockLocation location = new BlockLocation(x, y, z);
		BlockState oldBlockState  = blocks.get(x,y,z);
		int blockData = oldBlockState.getData();

		Block oldBlock = getBlockAt(location);
		blocks.set(x, y, z, new BlockState(id, blockData));

		Block newBlock = getBlockAt(location);
		EventBus eventBus = world.getBot().getEventBus();
		eventBus.fire(new BlockChangeEvent(world, location, oldBlock, newBlock));
	}

	public int getBlockMetadataAt(BlockLocation location) {
		return getBlockMetadataAt(location.getX(), location.getY(), location.getZ());
	}

	public int getBlockMetadataAt(int x, int y, int z) {
		return blocks.get(x, y, z).getData();
	}

	public void setBlockMetadataAt(int metadata, BlockLocation location) {
		setBlockMetadataAt(metadata, location.getX(), location.getY(), location.getZ());
	}

	public void setBlockMetadataAt(int metadata, int x, int y, int z) {
		BlockLocation location = new BlockLocation(x, y, z);
		BlockState oldBlockState  = blocks.get(x,y,z);
		int blockData = oldBlockState.getData();

		Block oldBlock = getBlockAt(location);
		blocks.set(x, y, z, new BlockState(oldBlockState.getId(), blockData));
		Block newBlock = getBlockAt(location);

		EventBus eventBus = world.getBot().getEventBus();
		eventBus.fire(new BlockChangeEvent(world, location, oldBlock, newBlock));
	}

	public int getBlockLightAt(BlockLocation location) {
		return getBlockLightAt(location.getX(), location.getY(), location.getZ());
	}

	public int getBlockLightAt(int x, int y, int z) {
		return light.get(x,y,z);
	}

	public int getBlockSkylightAt(BlockLocation location) {
		return getBlockSkylightAt(location.getX(), location.getY(), location.getZ());
	}

	public int getBlockSkylightAt(int x, int y, int z) {
		return skylight.get(x,y,z);
	}

	public BiomeType getBlockBiomeAt(BlockLocation location) {
		return getBlockBiomeAt(location.getX(), location.getY(), location.getZ());
	}

	public BiomeType getBlockBiomeAt(int x, int y, int z) {
		int index = z << 4 | x;
		if(index < 0 || index >= biomes.length)
			return null;
		return BiomeType.getById(biomes[index] & 0xFF);
	}

	public void setBlockBiomeAt(BiomeType biome, BlockLocation location) {
		setBlockBiomeAt(biome, location.getX(), location.getY(), location.getZ());
	}

	public void setBlockBiomeAt(BiomeType biome, int x, int y, int z) {
		int index = z << 4 | x;
		if(index < 0 || index >= biomes.length)
			return;
		biomes[index] = (byte) biome.getId();
	}
}
