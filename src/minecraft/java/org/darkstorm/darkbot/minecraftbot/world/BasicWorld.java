package org.darkstorm.darkbot.minecraftbot.world;

import java.lang.reflect.Constructor;
import java.util.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.*;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnData;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnLocation;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.ObjectEntitySpawnEvent.ObjectSpawnData;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.ObjectEntitySpawnEvent.ThrownObjectSpawnData;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.PaintingSpawnEvent.PaintingSpawnLocation;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.RotatedEntitySpawnEvent.RotatedSpawnLocation;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.BlockChangeEvent;
import org.darkstorm.darkbot.minecraftbot.events.world.*;
import org.darkstorm.darkbot.minecraftbot.events.world.ChunkLoadEvent;
import org.darkstorm.darkbot.minecraftbot.nbt.NBTTagCompound;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;
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

	private long time, age;

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
	public void onPlayerEquipmentUpdate(PlayerEquipmentUpdateEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		LivingEntity livingEntity = (LivingEntity) entity;
		livingEntity.setWornItemAt(event.getSlot(), event.getItem());
	}

	@EventHandler
	public void onTimeUpdate(TimeUpdateEvent event) {
		time = event.getTime();
		age = event.getWorldAge();
	}

	@EventHandler
	public void onRespawn(RespawnEvent event) {
		synchronized(chunks) {
			chunks.clear();
		}
	}

	@EventHandler
	public void onPlayerSpawn(PlayerSpawnEvent event) {
		RotatedSpawnLocation location = event.getLocation();
		PlayerEntity entity = new PlayerEntity(this, event.getEntityId(), event.getPlayerName());
		entity.setX(location.getX());
		entity.setY(location.getY());
		entity.setZ(location.getZ());
		entity.setYaw(location.getYaw());
		entity.setPitch(location.getPitch());
		entity.setWornItemAt(0, event.getHeldItem());
		if(event.getMetadata() != null)
			entity.updateMetadata(event.getMetadata());
		spawnEntity(entity);
	}

	@EventHandler
	public void onEntityCollect(EntityCollectEvent event) {
		Entity entity = getEntityById(event.getCollectedId());
		if(entity != null)
			despawnEntity(entity);
	}

	@EventHandler
	public void onObjectEntitySpawn(ObjectEntitySpawnEvent event) {
		RotatedSpawnLocation spawnLocation = event.getLocation();
		ObjectSpawnData spawnData = event.getSpawnData();
		Class<? extends Entity> entityClass = EntityList.getObjectEntityClass(spawnData.getType());
		if(entityClass == null)
			return;
		Entity entity;
		try {
			Constructor<? extends Entity> constructor = entityClass.getConstructor(World.class, Integer.TYPE);
			entity = constructor.newInstance(this, event.getEntityId());
		} catch(Exception exception) {
			exception.printStackTrace();
			return;
		}
		entity.setX(spawnLocation.getX());
		entity.setY(spawnLocation.getY());
		entity.setZ(spawnLocation.getZ());
		entity.setYaw(spawnLocation.getYaw());
		entity.setPitch(spawnLocation.getPitch());
		if(entity instanceof ThrownEntity && event.getSpawnData() instanceof ThrownObjectSpawnData) {
			Entity thrower = getEntityById(((ThrownObjectSpawnData) event.getSpawnData()).getThrowerId());
			if(thrower != null)
				((ThrownEntity) entity).setThrower(thrower);
		}
		spawnEntity(entity);
	}

	@EventHandler
	public void onLivingEntitySpawn(LivingEntitySpawnEvent event) {
		LivingEntitySpawnData spawnData = event.getSpawnData();
		LivingEntitySpawnLocation spawnLocation = event.getLocation();
		Class<? extends LivingEntity> entityClass = EntityList.getLivingEntityClass(spawnData.getType());
		if(entityClass == null)
			return;
		LivingEntity entity;
		try {
			Constructor<? extends LivingEntity> constructor = entityClass.getConstructor(World.class, Integer.TYPE);
			entity = constructor.newInstance(this, event.getEntityId());
		} catch(Exception exception) {
			exception.printStackTrace();
			return;
		}
		entity.setX(spawnLocation.getX());
		entity.setY(spawnLocation.getY());
		entity.setZ(spawnLocation.getZ());
		entity.setYaw(spawnLocation.getYaw());
		entity.setPitch(spawnLocation.getPitch());
		entity.setHeadYaw(spawnLocation.getHeadYaw());

		if(event.getMetadata() != null)
			entity.updateMetadata(event.getMetadata());
		spawnEntity(entity);
	}

	@EventHandler
	public void onPaintingSpawn(PaintingSpawnEvent event) {
		PaintingSpawnLocation spawnLocation = event.getLocation();
		PaintingEntity entity = new PaintingEntity(this, event.getEntityId(), ArtType.getArtTypeByName(event.getTitle()));
		entity.setX(spawnLocation.getX());
		entity.setY(spawnLocation.getY());
		entity.setZ(spawnLocation.getZ());
		entity.setDirection(spawnLocation.getDirection());
		spawnEntity(entity);
	}

	@EventHandler
	public void onEntityDespawn(EntityDespawnEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity != null) {
			despawnEntity(entity);
			entity.setDead(true);
		}
	}

	@EventHandler
	public void onEntityMove(EntityMoveEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity == null)
			return;
		entity.setX(entity.getX() + event.getX());
		entity.setY(entity.getY() + event.getY());
		entity.setZ(entity.getZ() + event.getZ());
	}

	@EventHandler
	public void onEntityRotate(EntityRotateEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity == null)
			return;
		entity.setYaw(event.getYaw());
		entity.setPitch(event.getPitch());
	}

	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity == null)
			return;
		entity.setX(event.getX());
		entity.setY(event.getY());
		entity.setZ(event.getZ());
		entity.setYaw(event.getYaw());
		entity.setPitch(event.getPitch());
	}

	@EventHandler
	public void onEntityHeadRotate(EntityHeadRotateEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		((LivingEntity) entity).setHeadYaw(event.getHeadYaw());
	}

	@EventHandler
	public void onEntityMount(EntityMountEvent event) {
		Entity rider = getEntityById(event.getEntityId());
		Entity riding = getEntityById(event.getMountedEntityId());
		if(rider == null || riding == null)
			return;
		rider.setRiding(riding);
		riding.setRider(rider);
	}

	@EventHandler
	public void onEntityDismount(EntityDismountEvent event) {
		Entity rider = getEntityById(event.getEntityId());
		if(rider == null)
			return;
		if(rider.getRiding() != null) {
			rider.getRiding().setRider(null);
			rider.setRiding(null);
		}
	}

	@EventHandler
	public void onEntityMetadataUpdate(EntityMetadataUpdateEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity == null)
			return;
		entity.updateMetadata(event.getMetadata());
	}

	@EventHandler
	public void onChunkLoad(org.darkstorm.darkbot.minecraftbot.events.protocol.server.ChunkLoadEvent event) {
		ChunkLocation location = new ChunkLocation(event.getX(), event.getY(), event.getZ());
		Chunk chunk = new Chunk(this, location, event.getBlocks(), event.getMetadata(), event.getLight(), event.getSkylight(), event.getBiomes());
		synchronized(chunks) {
			chunks.put(location, chunk);
		}
		bot.getEventManager().sendEvent(new ChunkLoadEvent(this, chunk));
	}

	@EventHandler
	public void onBlockChange(BlockChangeEvent event) {
		setBlockIdAt(event.getId(), event.getX(), event.getY(), event.getZ());
		setBlockMetadataAt(event.getMetadata(), event.getX(), event.getY(), event.getZ());
	}

	@EventHandler
	public void onTileEntityUpdate(TileEntityUpdateEvent event) {
		BlockLocation location = new BlockLocation(event.getX(), event.getY(), event.getZ());
		Class<? extends TileEntity> entityClass = EntityList.getTileEntityClass(event.getType());
		if(entityClass == null)
			return;
		TileEntity entity;
		try {
			Constructor<? extends TileEntity> constructor = entityClass.getConstructor(NBTTagCompound.class);
			entity = constructor.newInstance(event.getCompound());
		} catch(Exception exception) {
			exception.printStackTrace();
			return;
		}
		setTileEntityAt(entity, location);
	}

	@EventHandler
	public void onSignUpdate(SignUpdateEvent event) {
		BlockLocation location = new BlockLocation(event.getX(), event.getY(), event.getZ());
		setTileEntityAt(new SignTileEntity(location.getX(), location.getY(), location.getZ(), event.getText()), location);
	}

	@EventHandler
	public void onEditTileEntity(EditTileEntityEvent event) {
		TileEntity entity = getTileEntityAt(event.getX(), event.getY(), event.getZ());
		if(entity != null)
			if(entity instanceof SignTileEntity)
				bot.getEventManager().sendEvent(new EditSignEvent(entity.getLocation()));
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

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public long getAge() {
		return age;
	}
}