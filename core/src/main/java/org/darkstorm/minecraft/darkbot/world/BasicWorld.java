package org.darkstorm.minecraft.darkbot.world;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;

import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.event.general.TickEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.server.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.LivingEntitySpawnEvent.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.ObjectEntitySpawnEvent.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.BlockChangeEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.server.PaintingSpawnEvent.PaintingSpawnLocation;
import org.darkstorm.minecraft.darkbot.event.protocol.server.RotatedEntitySpawnEvent.RotatedSpawnLocation;
import org.darkstorm.minecraft.darkbot.event.world.*;
import org.darkstorm.minecraft.darkbot.event.world.ChunkLoadEvent;
import org.darkstorm.minecraft.darkbot.world.block.*;
import org.darkstorm.minecraft.darkbot.world.entity.*;
import org.darkstorm.minecraft.darkbot.world.pathfinding.*;
import org.darkstorm.minecraft.darkbot.world.pathfinding.astar.AStarPathSearchProvider;

public final class BasicWorld implements World, EventListener {
	private final MinecraftBot bot;
	private final WorldType type;
	private final int dimension;
	private final Difficulty difficulty;
	private final int height;
	private final Map<ChunkLocation, Chunk> chunks;
	private final List<PlayerInfo> players = new ArrayList<PlayerInfo>();
	private final List<Entity> entities;
	private PathSearchProvider pathFinder;

	private long time, age;

	public BasicWorld(MinecraftBot bot, WorldType type, int dimension, Difficulty difficulty, int height) {
		this.bot = bot;
		this.type = type;
		this.height = height;
		this.dimension = dimension;
		this.difficulty = difficulty;
		chunks = new HashMap<ChunkLocation, Chunk>();
		entities = new ArrayList<Entity>();
		pathFinder = new AStarPathSearchProvider(new EuclideanHeuristic(), new SimpleWorldPhysics(this));
		EventBus eventBus = bot.getEventBus();
		eventBus.register(this);
	}
	
	@EventHandler
	public void onTick(TickEvent event) {
		synchronized(entities) {
			for(Entity entity : entities)
				if(!entity.isDead())
					entity.update();
		}
	}

	@EventHandler
	public void onPlayerEquipmentUpdate(PlayerEquipmentUpdateEvent event) {
		Entity entity = getEntityById(event.getEntityId());
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		LivingEntity livingEntity = (LivingEntity) entity;
		livingEntity.setWornItemAt(event.getSlot().ordinal(), event.getItem());
	}

	@EventHandler
	public void onTimeUpdate(TimeUpdateEvent event) {
		time = event.getTime();
		age = event.getWorldAge();
	}

	@EventHandler
	public void onPlayerListUpdate(PlayerListUpdateEvent event) {
		PlayerInfo playerInfo = event.getPlayerInfo();
		synchronized(players) {
			for (PlayerInfo player : players) {
				if (player.getPlayerUUID().equals(playerInfo.getPlayerUUID())) {
					if(player.getPlayerName() == null && playerInfo.getPlayerName() != null)
						player.setPlayerName(playerInfo.getPlayerName());
					return;
				}
			}
			players.add(playerInfo);
		}
	}

	@EventHandler
	public void onPlayerListRemove(PlayerListRemoveEvent event) {
		PlayerInfo playerInfo = event.getPlayerInfo();
		synchronized(players) {
			for (PlayerInfo player : players) {
				if (player.getPlayerUUID().equals(playerInfo.getPlayerUUID())) {
					players.remove(player);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerSpawn(PlayerSpawnEvent event) {
		RotatedSpawnLocation location = event.getLocation();
		String playerName = null;
		synchronized(players) {
			for (PlayerInfo playerInfo : players) {
				if (playerInfo.getPlayerUUID().equals(event.getPlayerUUID()))
					playerName = playerInfo.getPlayerName();
			}
		}
		if(playerName == null) {
			System.out.println("Couldn't find spawned player in list!");
			return;
		}
		PlayerEntity entity = new PlayerEntity(this, event.getEntityId(), playerName);
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
		Class<? extends Entity> entityClass = EntityList.getObjectEntityClass(spawnData.getType().ordinal());
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
		Class<? extends LivingEntity> entityClass = EntityList.getLivingEntityClass(spawnData.getType().ordinal());
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
	public void onChunkLoad(org.darkstorm.minecraft.darkbot.event.protocol.server.ChunkLoadEvent event) {
		ChunkLocation location = new ChunkLocation(event.getX(), event.getY(), event.getZ());
		Chunk chunk = new Chunk(this, location, event.getBlocks(), event.getLight(), event.getSkylight(), event.getBiomes());
		synchronized(chunks) {
			chunks.put(location, chunk);
		}
		bot.getEventBus().fire(new ChunkLoadEvent(this, chunk));
	}

	@EventHandler
	public void onBlockChange(BlockChangeEvent event) {
		setBlockIdAt(event.getId(), event.getX(), event.getY(), event.getZ());
		setBlockMetadataAt(event.getMetadata(), event.getX(), event.getY(), event.getZ());
	}

	@EventHandler
	public void onTileEntityUpdate(TileEntityUpdateEvent event) {
		BlockLocation location = new BlockLocation(event.getX(), event.getY(), event.getZ());
		Class<? extends TileEntity> entityClass = EntityList.getTileEntityClass(event.getType().ordinal());

		if(entityClass == null)
			return;
		TileEntity entity;
		try {
			Constructor<? extends TileEntity> constructor = entityClass.getConstructor(CompoundTag.class);
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
				bot.getEventBus().fire(new EditSignEvent(entity.getLocation()));
	}

	@Override
	public void destroy() {
		EventBus eventBus = bot.getEventBus();
		eventBus.unregister(this);
		synchronized(entities) {
			for(Entity entity : entities)
				entity.setDead(true);
			entities.clear();
		}
		synchronized(chunks) {
			chunks.clear();
		}
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
		
		BlockLocation base = chunk.getBlockBaseLocation();
		return chunk.getBlockAt(
		                        location.getX() - base.getX(),
		                        location.getY() - base.getY(),
		                        location.getZ() - base.getZ());
	}
	

	@Override
	public int getBlockIdAt(int x, int y, int z) {
		return getBlockIdAt(new BlockLocation(x, y, z));
	}

	@Override
	public int getBlockIdAt(BlockLocation location) {

		ChunkLocation chunkLocation = new ChunkLocation(location);
		Chunk chunk = getChunkAt(chunkLocation);
		if(chunk == null) {
			System.out.println("Attempted to get invalid block id from: " + location);
			return -1;
		}


		BlockLocation base = chunk.getBlockBaseLocation();
		return chunk.getBlockIdAt(
				                   location.getX() - base.getX(),
				                   location.getY() - base.getY(),
				                   location.getZ() - base.getZ());
	}

	@Override
	public void setBlockIdAt(int id, int x, int y, int z) {
		setBlockIdAt(id, new BlockLocation(x, y, z));
	}

	@Override
	public void setBlockIdAt(int id, BlockLocation location) {
		ChunkLocation chunkLocation = new ChunkLocation(location);
		Chunk chunk = getChunkAt(chunkLocation);
		if(chunk == null) {
			System.out.println("Attempted to set invalid block id at: " + location);
			return;
		}

		BlockLocation base = chunk.getBlockBaseLocation();
		chunk.setBlockIdAt(id,
		                   location.getX() - base.getX(),
		                   location.getY() - base.getY(),
		                   location.getZ() - base.getZ());
	}

	@Override
	public int getBlockMetadataAt(int x, int y, int z) {
		return getBlockMetadataAt(new BlockLocation(x, y, z));
	}

	@Override
	public int getBlockMetadataAt(BlockLocation location) {
		ChunkLocation chunkLocation = new ChunkLocation(location);
		Chunk chunk = getChunkAt(chunkLocation);
		if(chunk == null) {
			System.out.println("Attempted to get invalid block metadata from: " + location);
			return -1;
		}

		BlockLocation base = chunk.getBlockBaseLocation();
		return chunk.getBlockMetadataAt(
		     		                   location.getX() - base.getX(),
		    		                   location.getY() - base.getY(),
		    		                   location.getZ() - base.getZ());
	}

	@Override
	public void setBlockMetadataAt(int metadata, int x, int y, int z) {
		setBlockMetadataAt(metadata, new BlockLocation(x, y, z));
	}

	@Override
	public void setBlockMetadataAt(int metadata, BlockLocation location) {
		ChunkLocation chunkLocation = new ChunkLocation(location);
		Chunk chunk = getChunkAt(chunkLocation);
		if(chunk == null) {
			System.out.println("Attempted to set invalid block metadata at: " + location);
			return;
		}

		BlockLocation base = chunk.getBlockBaseLocation();
		chunk.setBlockMetadataAt(metadata, 
				                   location.getX() - base.getX(),
				                   location.getY() - base.getY(),
				                   location.getZ() - base.getZ());
	}

	@Override
	public TileEntity getTileEntityAt(int x, int y, int z) {
		return getTileEntityAt(new BlockLocation(x, y, z));
	}

	@Override
	public TileEntity getTileEntityAt(BlockLocation location) {
		ChunkLocation chunkLocation = new ChunkLocation(location);
		Chunk chunk = getChunkAt(chunkLocation);
		if(chunk == null) {
			System.out.println("Attempted to get invalid tile entity from: " + location);
			return null;
		}

		BlockLocation base = chunk.getBlockBaseLocation();
		return chunk.getTileEntityAt(
		  		                   location.getX() - base.getX(),
				                   location.getY() - base.getY(),
				                   location.getZ() - base.getZ());
	}

	@Override
	public void setTileEntityAt(TileEntity tileEntity, int x, int y, int z) {
		setTileEntityAt(tileEntity, new BlockLocation(x, y, z));
	}

	@Override
	public void setTileEntityAt(TileEntity tileEntity, BlockLocation location) {
		ChunkLocation chunkLocation = new ChunkLocation(location);
		Chunk chunk = getChunkAt(chunkLocation);
		if(chunk == null) {
			System.out.println("Attempted to set invalid tile entity at: " + location);
			return;
		}

		BlockLocation base = chunk.getBlockBaseLocation();
		chunk.setTileEntityAt(tileEntity, 
			                   location.getX() - base.getX(),
			                   location.getY() - base.getY(),
			                   location.getZ() - base.getZ());
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
	public boolean isColliding(BoundingBox box) {
		int minX = (int) Math.floor(box.getMinX());
		int minY = (int) Math.floor(box.getMinY() - 1);
		int minZ = (int) Math.floor(box.getMinZ());
		int maxX = (int) Math.ceil(box.getMaxX());
		int maxY = (int) Math.ceil(box.getMaxY());
		int maxZ = (int) Math.ceil(box.getMaxZ());
		
		synchronized(chunks) {
			Chunk chunk = null;
			BlockLocation chunkBase = null;

			for(int x = minX; x < maxX; x++) {
				for(int z = minZ; z < maxZ; z++) {
					for(int y = minY; y < maxY; y++) {
						if(chunkBase == null || x < chunkBase.getX() || y < chunkBase.getY() || z < chunkBase.getZ() || x - chunkBase.getX() >= 16 || y - chunkBase.getY() >= 16 || z - chunkBase.getZ() >= 16) {
							ChunkLocation chunkLocation = new ChunkLocation(new BlockLocation(x, y, z));
							
							chunk = getChunkAt(chunkLocation);
							if(chunk != null)
								chunkBase = chunk.getBlockBaseLocation();
							else
								chunkBase = new BlockLocation(chunkLocation);
						}
						
						if(chunk != null) {
							Block block = chunk.getBlockAt(x - chunkBase.getX(), y - chunkBase.getY(), z - chunkBase.getZ());
							if(block == null)
								continue;
							
							boolean intersects = false;
							for(BoundingBox blockBox : block.getBoundingBoxes()) {
								if(box.intersectsWith(blockBox)) {
									intersects = true;
									break;
								}
							}
							if(!intersects)
								continue;
							
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public Set<Block> getCollidingBlocks(BoundingBox box) {
		Set<Block> blocks = new HashSet<>();
		int minX = (int) Math.floor(box.getMinX());
		int minY = (int) Math.floor(box.getMinY() - 1);
		int minZ = (int) Math.floor(box.getMinZ());
		int maxX = (int) Math.ceil(box.getMaxX());
		int maxY = (int) Math.ceil(box.getMaxY());
		int maxZ = (int) Math.ceil(box.getMaxZ());
		
		synchronized(chunks) {
			Chunk chunk = null;
			BlockLocation chunkBase = null;

			for(int x = minX; x < maxX; x++) {
				for(int z = minZ; z < maxZ; z++) {
					for(int y = minY; y < maxY; y++) {
						if(chunkBase == null || x < chunkBase.getX() || y < chunkBase.getY() || z < chunkBase.getZ() || x - chunkBase.getX() >= 16 || y - chunkBase.getY() >= 16 || z - chunkBase.getZ() >= 16) {
							ChunkLocation chunkLocation = new ChunkLocation(new BlockLocation(x, y, z));
							
							chunk = getChunkAt(chunkLocation);
							if(chunk != null)
								chunkBase = chunk.getBlockBaseLocation();
							else
								chunkBase = new BlockLocation(chunkLocation);
						}
						
						if(chunk != null) {
							Block block = chunk.getBlockAt(x - chunkBase.getX(), y - chunkBase.getY(), z - chunkBase.getZ());
							if(block == null)
								continue;
							
							boolean intersects = false;
							for(BoundingBox blockBox : block.getBoundingBoxes()) {
								if(box.intersectsWith(blockBox)) {
									intersects = true;
									break;
								}
							}
							if(!intersects)
								continue;
							
							blocks.add(block);
						}
					}
				}
			}
		}
		return blocks;
	}
	
	@Override
	public boolean isInMaterial(BoundingBox box, BlockType... materials) {
		int minX = (int) Math.floor(box.getMinX());
		int minY = (int) Math.floor(box.getMinY() - 1);
		int minZ = (int) Math.floor(box.getMinZ());
		int maxX = (int) Math.ceil(box.getMaxX());
		int maxY = (int) Math.ceil(box.getMaxY());
		int maxZ = (int) Math.ceil(box.getMaxZ());
		
		synchronized(chunks) {
			Chunk chunk = null;
			BlockLocation chunkBase = null;

			for(int x = minX; x < maxX; x++) {
				for(int z = minZ; z < maxZ; z++) {
					for(int y = minY; y < maxY; y++) {
						if(chunkBase == null || x < chunkBase.getX() || y < chunkBase.getY() || z < chunkBase.getZ() || x - chunkBase.getX() >= 16 || y - chunkBase.getY() >= 16 || z - chunkBase.getZ() >= 16) {
							ChunkLocation chunkLocation = new ChunkLocation(new BlockLocation(x, y, z));
							
							chunk = getChunkAt(chunkLocation);
							if(chunk != null)
								chunkBase = chunk.getBlockBaseLocation();
							else
								chunkBase = new BlockLocation(chunkLocation);
						}
						
						if(chunk != null) {
							Block block = chunk.getBlockAt(x - chunkBase.getX(), y - chunkBase.getY(), z - chunkBase.getZ());
							if(block == null)
								continue;
							
							boolean matches = false;
							for(BlockType material : materials) {
								if(material == block.getType()) {
									matches = true;
									break;
								}
							}
							if(!matches)
								continue;
							
							boolean intersects = true;
							for(BoundingBox blockBox : block.getBoundingBoxes()) {
								if(box.intersectsWith(blockBox)) {
									intersects = true;
									break;
								} else
									intersects = false;
							}
							if(!intersects)
								continue;
							
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public Entity[] getEntities() {
		synchronized(entities) {
			return entities.toArray(new Entity[entities.size()]);
		}
	}

	@Override
	public PlayerInfo[] getPlayers() {
		synchronized(players) {
			return players.toArray(new PlayerInfo[players.size()]);
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
	public int getDimension() {
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
	public void setPathFinder(PathSearchProvider pathFinder) {
		this.pathFinder = pathFinder;
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