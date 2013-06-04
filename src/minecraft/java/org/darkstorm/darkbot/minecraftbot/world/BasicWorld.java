package org.darkstorm.darkbot.minecraftbot.world;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.events.world.ChunkLoadEvent;
import org.darkstorm.darkbot.minecraftbot.nbt.NBTTagCompound;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet130UpdateSign;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.*;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;
import org.darkstorm.darkbot.minecraftbot.world.item.BasicItemStack;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.PathSearchProvider;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.astar.AStarPathSearchProvider;

public final class BasicWorld implements World, EventListener {
	private final MinecraftBot bot;
	private final WorldType type;
	private final Dimension dimension;
	private final Difficulty difficulty;
	private final int height;
	private final Map<ChunkLocation, Chunk> chunks;
	private final List<Entity> entities;
	private final PathSearchProvider pathFinder;

	public BasicWorld(MinecraftBot bot, WorldType type, Dimension dimension, Difficulty difficulty, int height) {
		this.bot = bot;
		this.type = type;
		this.height = height;
		this.dimension = dimension;
		this.difficulty = difficulty;
		chunks = new HashMap<ChunkLocation, Chunk>();
		entities = new ArrayList<Entity>();
		pathFinder = new AStarPathSearchProvider(this);
		EventManager eventManager = bot.getEventManager();
		eventManager.registerListener(this);
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet5PlayerInventory) {
			Packet5PlayerInventory inventoryPacket = (Packet5PlayerInventory) packet;
			Entity entity = getEntityById(inventoryPacket.entityID);
			if(entity == null || !(entity instanceof LivingEntity))
				return;
			LivingEntity livingEntity = (LivingEntity) entity;
			livingEntity.setWornItemAt(inventoryPacket.slot, inventoryPacket.item);
		} else if(packet instanceof Packet8UpdateHealth) {
			Packet8UpdateHealth updateHealthPacket = (Packet8UpdateHealth) packet;
			MainPlayerEntity player = bot.getPlayer();
			player.setHealth(updateHealthPacket.healthMP);
			player.setHunger(updateHealthPacket.food);
		} else if(packet instanceof Packet9Respawn) {
			synchronized(chunks) {
				chunks.clear();
			}
		} else if(packet instanceof Packet20NamedEntitySpawn) {
			Packet20NamedEntitySpawn spawnPacket = (Packet20NamedEntitySpawn) packet;
			PlayerEntity entity = new PlayerEntity(this, spawnPacket.entityId, spawnPacket.name);
			entity.setX(spawnPacket.xPosition / 32D);
			entity.setY(spawnPacket.yPosition / 32D);
			entity.setZ(spawnPacket.zPosition / 32D);
			entity.setYaw(spawnPacket.rotation);
			entity.setPitch(spawnPacket.pitch);
			entity.setWornItemAt(0, new BasicItemStack(spawnPacket.currentItem, 1, 0));
			spawnEntity(entity);
		} else if(packet instanceof Packet22Collect) {
			Entity entity = getEntityById(((Packet22Collect) packet).collectedEntityId);
			if(entity != null)
				despawnEntity(entity);
		} else if(packet instanceof Packet23VehicleSpawn) {
			Packet23VehicleSpawn spawnPacket = (Packet23VehicleSpawn) packet;
			Entity entity = null;
			Class<? extends Entity> entityClass = EntityList.getObjectEntityClass(spawnPacket.type);
			if(entityClass == null)
				return;
			try {
				Constructor<? extends Entity> constructor = entityClass.getConstructor(World.class, Integer.TYPE);
				entity = constructor.newInstance(this, spawnPacket.entityId);
			} catch(Exception exception) {
				exception.printStackTrace();
				return;
			}
			entity.setX(spawnPacket.xPosition / 32D);
			entity.setY(spawnPacket.yPosition / 32D);
			entity.setZ(spawnPacket.zPosition / 32D);
			entity.setYaw(0);
			entity.setPitch(0);
			spawnEntity(entity);
		} else if(packet instanceof Packet24MobSpawn) {
			Packet24MobSpawn mobSpawnPacket = (Packet24MobSpawn) packet;
			LivingEntity entity = null;
			Class<? extends LivingEntity> entityClass = EntityList.getLivingEntityClass(mobSpawnPacket.type);
			if(entityClass == null)
				return;
			try {
				Constructor<? extends LivingEntity> constructor = entityClass.getConstructor(World.class, Integer.TYPE);
				entity = constructor.newInstance(this, mobSpawnPacket.entityId);
			} catch(Exception exception) {
				exception.printStackTrace();
				return;
			}
			entity.setX(mobSpawnPacket.xPosition / 32D);
			entity.setY(mobSpawnPacket.yPosition / 32D);
			entity.setZ(mobSpawnPacket.zPosition / 32D);
			entity.setYaw((mobSpawnPacket.yaw * 360) / 256F);
			entity.setPitch((mobSpawnPacket.pitch * 360) / 256F);
			entity.setHeadYaw((mobSpawnPacket.headYaw * 360) / 256F);

			if(mobSpawnPacket.getMetadata() != null)
				entity.updateMetadata(mobSpawnPacket.getMetadata());
			spawnEntity(entity);
		} else if(packet instanceof Packet25EntityPainting) {
			Packet25EntityPainting paintingPacket = (Packet25EntityPainting) packet;
			PaintingEntity entity = new PaintingEntity(this, paintingPacket.entityId, ArtType.getArtTypeByName(paintingPacket.title));
			entity.setX(paintingPacket.xPosition);
			entity.setY(paintingPacket.yPosition);
			entity.setZ(paintingPacket.zPosition);
			entity.setDirection(paintingPacket.direction);
			spawnEntity(entity);
		} else if(packet instanceof Packet26EntityExpOrb) {

		} else if(packet instanceof Packet29DestroyEntity) {
			Packet29DestroyEntity destroyEntityPacket = (Packet29DestroyEntity) packet;
			for(int id : destroyEntityPacket.entityIds) {
				Entity entity = getEntityById(id);
				if(entity != null) {
					despawnEntity(entity);
					entity.setDead(true);
				}
			}
		} else if(packet instanceof Packet30Entity) {
			Packet30Entity entityPacket = (Packet30Entity) packet;
			Entity entity = getEntityById(entityPacket.entityId);
			if(entity == null)
				return;
			entity.setX(entity.getX() + (entityPacket.xPosition / 32D));
			entity.setY(entity.getY() + (entityPacket.yPosition / 32D));
			entity.setZ(entity.getZ() + (entityPacket.zPosition / 32D));
			if(packet instanceof Packet31RelEntityMove || packet instanceof Packet33RelEntityMoveLook) {
				entity.setYaw((entityPacket.yaw * 360) / 256F);
				entity.setPitch((entityPacket.pitch * 360) / 256F);
			}
		} else if(packet instanceof Packet34EntityTeleport) {
			Packet34EntityTeleport teleportPacket = (Packet34EntityTeleport) packet;
			Entity entity = getEntityById(teleportPacket.entityId);
			if(entity == null)
				return;
			entity.setX(teleportPacket.xPosition / 32D);
			entity.setY(teleportPacket.yPosition / 32D);
			entity.setZ(teleportPacket.zPosition / 32D);
			entity.setYaw((teleportPacket.yaw * 360) / 256F);
			entity.setPitch((teleportPacket.pitch * 360) / 256F);
		} else if(packet instanceof Packet35EntityHeadRotation) {
			Packet35EntityHeadRotation headRotatePacket = (Packet35EntityHeadRotation) packet;
			Entity entity = getEntityById(headRotatePacket.entityId);
			if(entity == null || !(entity instanceof LivingEntity))
				return;
			((LivingEntity) entity).setHeadYaw((headRotatePacket.headRotationYaw * 360) / 256F);
		} else if(packet instanceof Packet39AttachEntity) {
			Packet39AttachEntity attachEntityPacket = (Packet39AttachEntity) packet;
			Entity rider = getEntityById(attachEntityPacket.entityId);
			if(rider == null)
				return;
			Entity riding = null;
			if(attachEntityPacket.vehicleEntityId == -1) {
				if(rider.getRiding() != null) {
					rider.getRiding().setRider(null);
					rider.setRiding(null);
				}
			} else {
				riding = getEntityById(attachEntityPacket.vehicleEntityId);
				if(riding == null)
					return;
				rider.setRiding(riding);
				riding.setRider(rider);
			}
		} else if(packet instanceof Packet40EntityMetadata) {
			Packet40EntityMetadata metadataPacket = (Packet40EntityMetadata) packet;
			Entity entity = getEntityById(metadataPacket.entityId);
			if(entity == null)
				return;
			entity.updateMetadata(metadataPacket.getMetadata());
		} else if(packet instanceof Packet43Experience) {
			Packet43Experience experiencePacket = (Packet43Experience) packet;
			MainPlayerEntity player = bot.getPlayer();
			player.setExperienceLevel(experiencePacket.experienceLevel);
			player.setExperienceTotal(experiencePacket.experienceTotal);
		} else if(packet instanceof Packet51MapChunk) {
			if(bot.isMovementDisabled())
				return;
			Packet51MapChunk mapChunkPacket = (Packet51MapChunk) packet;
			processChunk(mapChunkPacket.x, mapChunkPacket.z, mapChunkPacket.chunkData, mapChunkPacket.bitmask, mapChunkPacket.additionalBitmask, true, mapChunkPacket.biomes);
		} else if(packet instanceof Packet52MultiBlockChange) {
			Packet52MultiBlockChange multiBlockChangePacket = (Packet52MultiBlockChange) packet;
			if(multiBlockChangePacket.metadataArray == null)
				return;
			DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(multiBlockChangePacket.metadataArray));
			try {
				for(int i = 0; i < multiBlockChangePacket.size; i++) {
					short word0 = datainputstream.readShort();
					short word1 = datainputstream.readShort();
					int id = (word1 & 0xfff) >> 4;
					int metadata = word1 & 0xf;
					int x = word0 >> 12 & 0xf;
					int z = word0 >> 8 & 0xf;
					int y = word0 & 0xff;
					setBlockIdAt(id, (multiBlockChangePacket.xPosition * 16) + x, y, (multiBlockChangePacket.zPosition * 16) + z);
					setBlockMetadataAt(metadata, (multiBlockChangePacket.xPosition * 16) + x, y, (multiBlockChangePacket.zPosition * 16) + z);
				}
			} catch(IOException exception) {
				exception.printStackTrace();
			}
		} else if(packet instanceof Packet53BlockChange) {
			Packet53BlockChange blockChangePacket = (Packet53BlockChange) packet;
			setBlockIdAt(blockChangePacket.type, blockChangePacket.xPosition, blockChangePacket.yPosition, blockChangePacket.zPosition);
			setBlockMetadataAt(blockChangePacket.metadata, blockChangePacket.xPosition, blockChangePacket.yPosition, blockChangePacket.zPosition);
		} else if(packet instanceof Packet56MapChunks) {
			if(bot.isMovementDisabled())
				return;
			Packet56MapChunks chunkPacket = (Packet56MapChunks) packet;
			for(int i = 0; i < chunkPacket.primaryBitmap.length; i++)
				processChunk(chunkPacket.chunkX[i], chunkPacket.chunkZ[i], chunkPacket.chunkData[i], chunkPacket.primaryBitmap[i], chunkPacket.secondaryBitmap[i], chunkPacket.skylight, true);
		} else if(packet instanceof Packet132TileEntityData) {
			Packet132TileEntityData tileEntityPacket = (Packet132TileEntityData) packet;
			BlockLocation location = new BlockLocation(tileEntityPacket.xPosition, tileEntityPacket.yPosition, tileEntityPacket.zPosition);
			TileEntity entity;
			Class<? extends TileEntity> entityClass = EntityList.getTileEntityClass(tileEntityPacket.actionType);
			if(entityClass == null)
				return;
			try {
				Constructor<? extends TileEntity> constructor = entityClass.getConstructor(NBTTagCompound.class);
				entity = constructor.newInstance(tileEntityPacket.compound);
			} catch(Exception exception) {
				exception.printStackTrace();
				return;
			}
			setTileEntityAt(entity, location);
		} else if(packet instanceof Packet130UpdateSign) {
			Packet130UpdateSign signPacket = (Packet130UpdateSign) packet;
			BlockLocation location = new BlockLocation(signPacket.x, signPacket.y, signPacket.z);
			setTileEntityAt(new SignTileEntity(location.getX(), location.getY(), location.getZ(), signPacket.text), location);
		}
	}

	private void processChunk(int x, int z, byte[] data, int bitmask, int additionalBitmask, boolean addSkylight, boolean addBiomes) {
		if(data == null)
			return;
		int chunksChanged = 0;
		for(int i = 0; i < 16; i++)
			if((bitmask & (1 << i)) != 0)
				chunksChanged++;
		if(chunksChanged == 0)
			return;
		byte[] biomes = new byte[256];
		synchronized(chunks) {
			int i = 0;
			for(int y = 0; y < 16; y++) {
				if((bitmask & (1 << y)) == 0)
					continue;
				int dataIndex = i * 4096;
				byte[] blocks = Arrays.copyOfRange(data, dataIndex, dataIndex + 4096);
				dataIndex += ((chunksChanged - i) * 4096) + (i * 2048);
				byte[] metadata = Arrays.copyOfRange(data, dataIndex, dataIndex + 2048);
				dataIndex += chunksChanged * 2048;
				byte[] light = Arrays.copyOfRange(data, dataIndex, dataIndex + 2048);
				dataIndex += chunksChanged * 2048;
				byte[] skylight = null;
				if(addSkylight)
					skylight = Arrays.copyOfRange(data, dataIndex, dataIndex + 2048);

				byte[] perBlockMetadata = new byte[4096];
				byte[] perBlockLight = new byte[4096];
				byte[] perBlockSkylight = new byte[4096];

				for(int j = 0; j < 2048; j++) {
					int k = j * 2;
					perBlockMetadata[k] = (byte) (metadata[j] & 0x0F);
					perBlockLight[k] = (byte) (light[j] & 0x0F);
					if(addSkylight)
						perBlockSkylight[k] = (byte) (skylight[j] & 0x0F);
					k++;
					perBlockMetadata[k] = (byte) (metadata[j] >> 4);
					perBlockLight[k] = (byte) (light[j] >> 4);
					if(addSkylight)
						perBlockSkylight[k] = (byte) (skylight[j] >> 4);
				}

				ChunkLocation newLocation = new ChunkLocation(x, y, z);
				Chunk chunk = new Chunk(this, newLocation, blocks, perBlockMetadata, perBlockLight, perBlockSkylight, biomes);
				chunks.put(newLocation, chunk);
				bot.getEventManager().sendEvent(new ChunkLoadEvent(this, chunk));
				i++;
			}
			System.arraycopy(data, data.length - 256, biomes, 0, 256);
		}
	}

	@Override
	public void destroy() {
		EventManager eventManager = bot.getEventManager();
		eventManager.unregisterListener(this);
		synchronized(entities) {
			for(Entity entity : entities)
				entity.setDead(true);
			entities.clear();
		}
		synchronized(chunks) {
			chunks.clear();
		}
		System.gc();
	}

	@Override
	public MinecraftBot getBot() {
		return bot;
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		return getBlockAt(new BlockLocation(x, y, z));
	}

	@Override
	public Block getBlockAt(BlockLocation location) {
		ChunkLocation chunkLocation = new ChunkLocation(location);
		Chunk chunk = getChunkAt(chunkLocation);
		if(chunk == null)
			return null;
		BlockLocation chunkBlockOffset = new BlockLocation(chunkLocation);
		int chunkOffsetX = location.getX() - chunkBlockOffset.getX();
		int chunkOffsetY = location.getY() - chunkBlockOffset.getY();
		int chunkOffsetZ = location.getZ() - chunkBlockOffset.getZ();
		int id = chunk.getBlockIdAt(chunkOffsetX, chunkOffsetY, chunkOffsetZ);
		int metadata = chunk.getBlockMetadataAt(chunkOffsetX, chunkOffsetY, chunkOffsetZ);
		return new Block(this, chunk, location, id, metadata);
	}

	@Override
	public int getBlockIdAt(int x, int y, int z) {
		return getBlockIdAt(new BlockLocation(x, y, z));
	}

	@Override
	public int getBlockIdAt(BlockLocation blockLocation) {
		ChunkLocation location = new ChunkLocation(blockLocation);
		BlockLocation chunkBlockOffset = new BlockLocation(location);
		Chunk chunk = getChunkAt(location);
		if(chunk == null)
			return 0;
		int id = chunk.getBlockIdAt(blockLocation.getX() - chunkBlockOffset.getX(), blockLocation.getY() - chunkBlockOffset.getY(), blockLocation.getZ() - chunkBlockOffset.getZ());
		return id;
	}

	@Override
	public void setBlockIdAt(int id, int x, int y, int z) {
		setBlockIdAt(id, new BlockLocation(x, y, z));
	}

	@Override
	public void setBlockIdAt(int id, BlockLocation blockLocation) {
		ChunkLocation location = new ChunkLocation(blockLocation);
		BlockLocation chunkBlockOffset = new BlockLocation(location);
		Chunk chunk = getChunkAt(location);
		if(chunk == null)
			return;
		chunk.setBlockIdAt(id, blockLocation.getX() - chunkBlockOffset.getX(), blockLocation.getY() - chunkBlockOffset.getY(), blockLocation.getZ() - chunkBlockOffset.getZ());
	}

	@Override
	public int getBlockMetadataAt(int x, int y, int z) {
		return getBlockMetadataAt(new BlockLocation(x, y, z));
	}

	@Override
	public int getBlockMetadataAt(BlockLocation blockLocation) {
		ChunkLocation location = new ChunkLocation(blockLocation);
		BlockLocation chunkBlockOffset = new BlockLocation(location);
		Chunk chunk = getChunkAt(location);
		if(chunk == null)
			return 0;
		int metadata = chunk.getBlockMetadataAt(blockLocation.getX() - chunkBlockOffset.getX(), blockLocation.getY() - chunkBlockOffset.getY(), blockLocation.getZ() - chunkBlockOffset.getZ());
		return metadata;
	}

	@Override
	public void setBlockMetadataAt(int metadata, int x, int y, int z) {
		setBlockMetadataAt(metadata, new BlockLocation(x, y, z));
	}

	@Override
	public void setBlockMetadataAt(int metadata, BlockLocation blockLocation) {
		ChunkLocation location = new ChunkLocation(blockLocation);
		BlockLocation chunkBlockOffset = new BlockLocation(location);
		Chunk chunk = getChunkAt(location);
		if(chunk == null)
			return;
		chunk.setBlockMetadataAt(metadata, blockLocation.getX() - chunkBlockOffset.getX(), blockLocation.getY() - chunkBlockOffset.getY(), blockLocation.getZ() - chunkBlockOffset.getZ());
	}

	@Override
	public TileEntity getTileEntityAt(int x, int y, int z) {
		return getTileEntityAt(new BlockLocation(x, y, z));
	}

	@Override
	public TileEntity getTileEntityAt(BlockLocation blockLocation) {
		ChunkLocation location = new ChunkLocation(blockLocation);
		BlockLocation chunkBlockOffset = new BlockLocation(location);
		Chunk chunk = getChunkAt(location);
		if(chunk == null)
			return null;
		TileEntity tileEntity = chunk.getTileEntityAt(blockLocation.getX() - chunkBlockOffset.getX(), blockLocation.getY() - chunkBlockOffset.getY(), blockLocation.getZ() - chunkBlockOffset.getZ());
		return tileEntity;
	}

	@Override
	public void setTileEntityAt(TileEntity tileEntity, int x, int y, int z) {
		setTileEntityAt(tileEntity, new BlockLocation(x, y, z));
	}

	@Override
	public void setTileEntityAt(TileEntity tileEntity, BlockLocation blockLocation) {
		ChunkLocation location = new ChunkLocation(blockLocation);
		BlockLocation chunkBlockOffset = new BlockLocation(location);
		Chunk chunk = getChunkAt(location);
		if(chunk == null)
			return;
		chunk.setTileEntityAt(tileEntity, blockLocation.getX() - chunkBlockOffset.getX(), blockLocation.getY() - chunkBlockOffset.getY(), blockLocation.getZ() - chunkBlockOffset.getZ());
	}

	@Override
	public Chunk getChunkAt(int x, int y, int z) {
		return getChunkAt(new ChunkLocation(x, y, z));
	}

	@Override
	public Chunk getChunkAt(ChunkLocation location) {
		synchronized(chunks) {
			return chunks.get(location);
		}
	}

	@Override
	public Entity[] getEntities() {
		synchronized(entities) {
			return entities.toArray(new Entity[entities.size()]);
		}
	}

	@Override
	public void spawnEntity(Entity entity) {
		if(entity == null)
			throw new NullPointerException();
		synchronized(entities) {
			if(!entities.contains(entity))
				entities.add(entity);
		}
	}

	@Override
	public Entity getEntityById(int id) {
		synchronized(entities) {
			for(Entity entity : entities)
				if(id == entity.getId())
					return entity;
			return null;
		}
	}

	@Override
	public void despawnEntity(Entity entity) {
		if(entity == null)
			throw new NullPointerException();
		synchronized(entities) {
			entities.remove(entity);
		}
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}

	@Override
	public Difficulty getDifficulty() {
		return difficulty;
	}

	@Override
	public WorldType getType() {
		return type;
	}

	@Override
	public int getMaxHeight() {
		return height;
	}

	@Override
	public PathSearchProvider getPathFinder() {
		return pathFinder;
	}
}