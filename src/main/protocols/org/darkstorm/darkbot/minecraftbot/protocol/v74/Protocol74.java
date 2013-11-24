package org.darkstorm.darkbot.minecraftbot.protocol.v74;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;

import javax.crypto.SecretKey;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.ai.BlockPlaceEvent;
import org.darkstorm.darkbot.minecraftbot.auth.*;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.io.*;
import org.darkstorm.darkbot.minecraftbot.events.protocol.client.*;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.*;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.EntitySpawnEvent.SpawnLocation;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnData;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnLocation;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.ObjectEntitySpawnEvent.ObjectSpawnData;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.ObjectEntitySpawnEvent.ThrownObjectSpawnData;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.PaintingSpawnEvent.PaintingSpawnLocation;
import org.darkstorm.darkbot.minecraftbot.events.protocol.server.RotatedEntitySpawnEvent.RotatedSpawnLocation;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v74.packets.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v74.packets.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.v74.packets.Packet19EntityAction.Action;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public final class Protocol74 extends AbstractProtocol implements EventListener {
	public static final int VERSION = 74;
	public static final String VERSION_NAME = "1.6.2";

	private final MinecraftBot bot;

	public Protocol74(MinecraftBot bot) {
		super(VERSION);
		this.bot = bot;

		register(Packet0KeepAlive.class);
		register(Packet1Login.class);
		register(Packet2Handshake.class);
		register(Packet3Chat.class);
		register(Packet4UpdateTime.class);
		register(Packet5PlayerInventory.class);
		register(Packet6SpawnPosition.class);
		register(Packet7UseEntity.class);
		register(Packet8UpdateHealth.class);
		register(Packet9Respawn.class);
		register(Packet10Flying.class);
		register(Packet11PlayerPosition.class);
		register(Packet12PlayerLook.class);
		register(Packet13PlayerLookMove.class);
		register(Packet14BlockDig.class);
		register(Packet15Place.class);
		register(Packet16BlockItemSwitch.class);
		register(Packet17Sleep.class);
		register(Packet18Animation.class);
		register(Packet19EntityAction.class);
		register(Packet20NamedEntitySpawn.class);
		register(Packet21PickupSpawn.class);
		register(Packet22Collect.class);
		register(Packet23VehicleSpawn.class);
		register(Packet24MobSpawn.class);
		register(Packet25EntityPainting.class);
		register(Packet26EntityExpOrb.class);
		register(Packet27SteerVehicle.class);
		register(Packet28EntityVelocity.class);
		register(Packet29DestroyEntity.class);
		register(Packet30Entity.class);
		register(Packet31RelEntityMove.class);
		register(Packet32EntityLook.class);
		register(Packet33RelEntityMoveLook.class);
		register(Packet34EntityTeleport.class);
		register(Packet35EntityHeadRotation.class);
		register(Packet38EntityStatus.class);
		register(Packet39AttachEntity.class);
		register(Packet40EntityMetadata.class);
		register(Packet41EntityEffect.class);
		register(Packet42RemoveEntityEffect.class);
		register(Packet43Experience.class);
		register(Packet44EntityProperties.class);
		register(Packet51MapChunk.class);
		register(Packet52MultiBlockChange.class);
		register(Packet53BlockChange.class);
		register(Packet54PlayNoteBlock.class);
		register(Packet55BlockDestroy.class);
		register(Packet56MapChunks.class);
		register(Packet60Explosion.class);
		register(Packet61DoorChange.class);
		register(Packet62NamedSoundEffect.class);
		register(Packet63Particle.class);
		register(Packet70ChangeGameState.class);
		register(Packet71Weather.class);
		register(Packet100OpenWindow.class);
		register(Packet101CloseWindow.class);
		register(Packet102WindowClick.class);
		register(Packet103SetSlot.class);
		register(Packet104WindowItems.class);
		register(Packet105UpdateProgressbar.class);
		register(Packet106Transaction.class);
		register(Packet107CreativeSetSlot.class);
		register(Packet108EnchantItem.class);
		register(Packet130UpdateSign.class);
		register(Packet131MapData.class);
		register(Packet132TileEntityData.class);
		register(Packet133OpenTileEditor.class);
		register(Packet200Statistic.class);
		register(Packet201PlayerInfo.class);
		register(Packet202PlayerAbilities.class);
		register(Packet203AutoComplete.class);
		register(Packet204ClientInfo.class);
		register(Packet205ClientCommand.class);
		register(Packet206SetObjective.class);
		register(Packet207SetScore.class);
		register(Packet208SetDisplayObjective.class);
		register(Packet209SetPlayerTeam.class);
		register(Packet250CustomPayload.class);
		register(Packet252SharedKey.class);
		register(Packet253EncryptionKeyRequest.class);
		register(Packet254ServerPing.class);
		register(Packet255KickDisconnect.class);

		bot.getEventManager().registerListener(this);
	}

	@EventHandler
	public void onHandshake(HandshakeEvent event) {
		bot.getConnectionHandler().sendPacket(new Packet2Handshake(VERSION, event.getSession().getUsername(), event.getServer(), event.getPort()));
	}

	@EventHandler
	public void onInventoryChange(InventoryChangeEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet102WindowClick packet = new Packet102WindowClick();
		packet.windowId = event.getInventory().getWindowId();
		packet.slot = event.getSlot();
		packet.button = event.getButton();
		packet.action = event.getTransactionId();
		packet.item = event.getItem();
		packet.shift = event.isShiftHeld();
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet101CloseWindow(event.getInventory().getWindowId()));
	}

	@EventHandler
	public void onHeldItemDrop(HeldItemDropEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet14BlockDig(event.isEntireStack() ? 3 : 4, 0, 0, 0, 0));
	}

	@EventHandler
	public void onHeldItemChange(HeldItemChangeEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet16BlockItemSwitch(event.getNewSlot()));
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		int mode;
		if(event instanceof EntityHitEvent)
			mode = 1;
		else if(event instanceof EntityUseEvent)
			mode = 0;
		else
			return;
		handler.sendPacket(new Packet7UseEntity(bot.getPlayer().getId(), event.getEntity().getId(), mode));
	}

	@EventHandler
	public void onArmSwing(ArmSwingEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet18Animation(bot.getPlayer().getId(), Animation.SWING_ARM));
	}

	@EventHandler
	public void onCrouchUpdate(CrouchUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet19EntityAction(bot.getPlayer().getId(), event.isCrouching() ? Action.CROUCH : Action.UNCROUCH, 0));
	}

	@EventHandler
	public void onSprintUpdate(SprintUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet19EntityAction(bot.getPlayer().getId(), event.isSprinting() ? Action.START_SPRINTING : Action.STOP_SPRINTING, 0));
	}

	@EventHandler
	public void onBedLeave(BedLeaveEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet19EntityAction(bot.getPlayer().getId(), Action.LEAVE_BED, 0));
	}

	@EventHandler
	public void onChatSent(ChatSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet3Chat(event.getMessage()));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		int code;
		if(event instanceof BlockBreakStartEvent)
			code = 0;
		else if(event instanceof BlockBreakStopEvent)
			code = 1;
		else if(event instanceof BlockBreakCompleteEvent)
			code = 2;
		else
			return;
		handler.sendPacket(new Packet14BlockDig(code, event.getX(), event.getY(), event.getZ(), event.getFace()));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet15Place packet = new Packet15Place();
		packet.xPosition = event.getX();
		packet.yPosition = event.getY();
		packet.zPosition = event.getZ();
		packet.direction = event.getFace();
		packet.xOffset = event.getXOffset();
		packet.yOffset = event.getYOffset();
		packet.zOffset = event.getZOffset();
		packet.itemStack = event.getItem();
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onPlayerUpdate(PlayerUpdateEvent event) {
		MainPlayerEntity player = event.getEntity();
		double x = player.getX(), y = player.getY(), z = player.getZ(), yaw = player.getYaw(), pitch = player.getPitch();
		boolean move = x != player.getLastX() || y != player.getLastY() || z != player.getLastZ();
		boolean rotate = yaw != player.getLastYaw() || pitch != player.getLastPitch();
		boolean onGround = player.isOnGround();
		Packet10Flying packet;
		if(move && rotate)
			packet = new Packet13PlayerLookMove(x, y, y + 1.62000000476837, z, (float) yaw, (float) pitch, onGround);
		else if(move)
			packet = new Packet11PlayerPosition(x, y, y + 1.62000000476837, z, onGround);
		else if(rotate)
			packet = new Packet12PlayerLook((float) yaw, (float) pitch, onGround);
		else
			packet = new Packet10Flying(onGround);
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onItemUse(ItemUseEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet15Place placePacket = new Packet15Place();
		placePacket.xPosition = -1;
		placePacket.yPosition = -1;
		placePacket.zPosition = -1;
		placePacket.itemStack = event.getItem();
		if(placePacket.itemStack != null && placePacket.itemStack.getId() == 346)
			placePacket.direction = 255;
		else
			placePacket.direction = -1;
		handler.sendPacket(placePacket);
	}

	@EventHandler
	public void onRequestRespawn(RequestRespawnEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet205ClientCommand(1));
	}

	@EventHandler
	public void onRequestDisconnect(RequestDisconnectEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet255KickDisconnect(event.getReason()));
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		EventManager eventManager = bot.getEventManager();

		switch(packet.getId()) {
		// Awkward brace style to prevent accidental field name overlap, and
		// switch rather than instanceof for efficiency
		case 0: {
			Packet0KeepAlive keepAlivePacket = (Packet0KeepAlive) packet;
			connectionHandler.sendPacket(keepAlivePacket);
			break;
		}
		case 1: {
			Packet1Login loginPacket = (Packet1Login) packet;
			eventManager.sendEvent(new LoginEvent(loginPacket.playerId, loginPacket.worldType, loginPacket.gameMode, loginPacket.dimension, loginPacket.difficulty, loginPacket.worldHeight, loginPacket.maxPlayers));
			connectionHandler.sendPacket(new Packet204ClientInfo("en_US", 1, 0, true, 2, true));
			break;
		}
		case 3: {
			Packet3Chat chatPacket = (Packet3Chat) packet;
			String message = chatPacket.message;
			try {
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(message);
				// TODO: Actually attempt to handle new chat format
				if(json.containsKey("text"))
					eventManager.sendEvent(new ChatReceivedEvent((String) json.get("text")));
			} catch(Exception exception) {}
			break;
		}
		case 4: {
			Packet4UpdateTime timePacket = (Packet4UpdateTime) packet;
			eventManager.sendEvent(new TimeUpdateEvent(timePacket.time, timePacket.otherTime));
			break;
		}
		case 5: {
			Packet5PlayerInventory inventoryPacket = (Packet5PlayerInventory) packet;
			eventManager.sendEvent(new PlayerEquipmentUpdateEvent(inventoryPacket.entityID, inventoryPacket.slot, inventoryPacket.item));
			break;
		}
		case 8: {
			Packet8UpdateHealth updateHealthPacket = (Packet8UpdateHealth) packet;
			int health = (int) Math.ceil(updateHealthPacket.healthMP);
			eventManager.sendEvent(new HealthUpdateEvent(health, updateHealthPacket.food, updateHealthPacket.foodSaturation));
			break;
		}
		case 9: {
			Packet9Respawn respawnPacket = (Packet9Respawn) packet;
			eventManager.sendEvent(new RespawnEvent(respawnPacket.respawnDimension, respawnPacket.difficulty, respawnPacket.gameMode, respawnPacket.worldType, respawnPacket.worldHeight));
			break;
		}
		case 13: {
			Packet13PlayerLookMove lookMovePacket = (Packet13PlayerLookMove) packet;
			connectionHandler.sendPacket(lookMovePacket);
			eventManager.sendEvent(new TeleportEvent(lookMovePacket.x, lookMovePacket.y, lookMovePacket.z, lookMovePacket.stance, lookMovePacket.yaw, lookMovePacket.pitch));
			break;
		}
		case 17: {
			Packet17Sleep sleepPacket = (Packet17Sleep) packet;
			eventManager.sendEvent(new SleepEvent(sleepPacket.entityID, sleepPacket.bedX, sleepPacket.bedY, sleepPacket.bedZ));
			break;
		}
		case 18: {
			Packet18Animation animationPacket = (Packet18Animation) packet;
			if(animationPacket.animation == Animation.EAT_FOOD)
				eventManager.sendEvent(new EntityEatEvent(animationPacket.entityId));
			break;
		}
		case 20: {
			Packet20NamedEntitySpawn spawnPacket = (Packet20NamedEntitySpawn) packet;
			RotatedSpawnLocation location = new RotatedSpawnLocation(spawnPacket.xPosition / 32D, spawnPacket.yPosition / 32D, spawnPacket.zPosition / 32D, (spawnPacket.rotation * 360) / 256F, (spawnPacket.pitch * 360) / 256F);
			ItemStack heldItem = new BasicItemStack(spawnPacket.currentItem, 1, 0);
			eventManager.sendEvent(new PlayerSpawnEvent(spawnPacket.entityId, spawnPacket.name, heldItem, location, spawnPacket.data));
			break;
		}
		case 22: {
			Packet22Collect collectPacket = (Packet22Collect) packet;
			eventManager.sendEvent(new EntityCollectEvent(collectPacket.collectedEntityId, collectPacket.collectorEntityId));
			break;
		}
		case 23: {
			Packet23VehicleSpawn spawnPacket = (Packet23VehicleSpawn) packet;
			RotatedSpawnLocation location = new RotatedSpawnLocation(spawnPacket.xPosition / 32D, spawnPacket.yPosition / 32D, spawnPacket.zPosition / 32D, (spawnPacket.yaw * 360) / 256F, (spawnPacket.pitch * 360) / 256F);
			ObjectSpawnData spawnData;
			if(spawnPacket.throwerEntityId != 0)
				spawnData = new ThrownObjectSpawnData(spawnPacket.type, spawnPacket.throwerEntityId, spawnPacket.speedX / 8000D, spawnPacket.speedY / 8000D, spawnPacket.speedZ / 8000D);
			else
				spawnData = new ObjectSpawnData(spawnPacket.type);
			eventManager.sendEvent(new ObjectEntitySpawnEvent(spawnPacket.entityId, location, spawnData));
			break;
		}
		case 24: {
			Packet24MobSpawn spawnPacket = (Packet24MobSpawn) packet;
			LivingEntitySpawnLocation location = new LivingEntitySpawnLocation(spawnPacket.xPosition / 32D, spawnPacket.yPosition / 32D, spawnPacket.zPosition / 32D, (spawnPacket.yaw * 360) / 256F, (spawnPacket.pitch * 360) / 256F, (spawnPacket.headYaw * 360) / 256F);
			LivingEntitySpawnData data = new LivingEntitySpawnData(spawnPacket.type, spawnPacket.velocityX / 8000D, spawnPacket.velocityY / 8000D, spawnPacket.velocityZ / 8000D);
			eventManager.sendEvent(new LivingEntitySpawnEvent(spawnPacket.entityId, location, data, spawnPacket.metadata));
			break;
		}
		case 25: {
			Packet25EntityPainting spawnPacket = (Packet25EntityPainting) packet;
			PaintingSpawnLocation location = new PaintingSpawnLocation(spawnPacket.xPosition, spawnPacket.yPosition, spawnPacket.zPosition, spawnPacket.direction);
			eventManager.sendEvent(new PaintingSpawnEvent(spawnPacket.entityId, location, spawnPacket.title));
			break;
		}
		case 26: {
			Packet26EntityExpOrb spawnPacket = (Packet26EntityExpOrb) packet;
			SpawnLocation location = new SpawnLocation(spawnPacket.posX / 32D, spawnPacket.posY / 32D, spawnPacket.posZ / 32D);
			eventManager.sendEvent(new ExpOrbSpawnEvent(spawnPacket.entityId, location, spawnPacket.xpValue));
			break;
		}
		case 28: {
			Packet28EntityVelocity velocityPacket = (Packet28EntityVelocity) packet;
			eventManager.sendEvent(new EntityVelocityEvent(velocityPacket.entityId, velocityPacket.motionX / 8000D, velocityPacket.motionY / 8000D, velocityPacket.motionZ / 8000D));
			break;
		}
		case 29: {
			Packet29DestroyEntity destroyEntityPacket = (Packet29DestroyEntity) packet;
			for(int id : destroyEntityPacket.entityIds)
				eventManager.sendEvent(new EntityDespawnEvent(id));
			break;
		}
		case 30:
		case 31:
		case 32:
		case 33: {
			Packet30Entity entityPacket = (Packet30Entity) packet;
			if(packet instanceof Packet31RelEntityMove || packet instanceof Packet33RelEntityMoveLook)
				eventManager.sendEvent(new EntityMoveEvent(entityPacket.entityId, entityPacket.xPosition / 32D, entityPacket.yPosition / 32D, entityPacket.zPosition / 32D));
			if(packet instanceof Packet32EntityLook || packet instanceof Packet33RelEntityMoveLook)
				eventManager.sendEvent(new EntityRotateEvent(entityPacket.entityId, (entityPacket.yaw * 360) / 256F, (entityPacket.pitch * 360) / 256F));
			break;
		}
		case 34: {
			Packet34EntityTeleport teleportPacket = (Packet34EntityTeleport) packet;
			eventManager.sendEvent(new EntityTeleportEvent(teleportPacket.entityId, teleportPacket.xPosition / 32D, teleportPacket.yPosition / 32D, teleportPacket.zPosition / 32D, (teleportPacket.yaw * 360) / 256F, (teleportPacket.pitch * 360) / 256F));
			break;
		}
		case 35: {
			Packet35EntityHeadRotation headRotatePacket = (Packet35EntityHeadRotation) packet;
			eventManager.sendEvent(new EntityHeadRotateEvent(headRotatePacket.entityId, (headRotatePacket.headRotationYaw * 360) / 256F));
			break;
		}
		case 38: {
			Packet38EntityStatus statusPacket = (Packet38EntityStatus) packet;
			if(statusPacket.entityStatus == 2)
				eventManager.sendEvent(new EntityHurtEvent(statusPacket.entityId));
			else if(statusPacket.entityStatus == 3)
				eventManager.sendEvent(new EntityDeathEvent(statusPacket.entityId));
			else if(statusPacket.entityStatus == 9)
				eventManager.sendEvent(new EntityStopEatingEvent(statusPacket.entityId));
			break;
		}
		case 39: {
			Packet39AttachEntity attachEntityPacket = (Packet39AttachEntity) packet;
			if(attachEntityPacket.leashed)
				break;
			if(attachEntityPacket.vehicleEntityId != -1)
				eventManager.sendEvent(new EntityMountEvent(attachEntityPacket.entityId, attachEntityPacket.vehicleEntityId));
			else
				eventManager.sendEvent(new EntityDismountEvent(attachEntityPacket.entityId));
			break;
		}
		case 40: {
			Packet40EntityMetadata metadataPacket = (Packet40EntityMetadata) packet;
			eventManager.sendEvent(new EntityMetadataUpdateEvent(metadataPacket.entityId, metadataPacket.metadata));
			break;
		}
		case 43: {
			Packet43Experience experiencePacket = (Packet43Experience) packet;
			eventManager.sendEvent(new ExperienceUpdateEvent(experiencePacket.experienceLevel, experiencePacket.experienceTotal));
			break;
		}
		case 51: {
			Packet51MapChunk mapChunkPacket = (Packet51MapChunk) packet;
			processChunk(mapChunkPacket.x, mapChunkPacket.z, mapChunkPacket.chunkData, mapChunkPacket.bitmask, mapChunkPacket.additionalBitmask, true, mapChunkPacket.biomes);
			break;
		}
		case 52: {
			Packet52MultiBlockChange multiBlockChangePacket = (Packet52MultiBlockChange) packet;
			if(multiBlockChangePacket.metadataArray == null)
				return;
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(multiBlockChangePacket.metadataArray));
			try {
				for(int i = 0; i < multiBlockChangePacket.size; i++) {
					short word0 = in.readShort();
					short word1 = in.readShort();
					int id = (word1 & 0xfff) >> 4;
					int metadata = word1 & 0xf;
					int x = word0 >> 12 & 0xf;
					int z = word0 >> 8 & 0xf;
					int y = word0 & 0xff;
					eventManager.sendEvent(new BlockChangeEvent(id, metadata, (multiBlockChangePacket.xPosition * 16) + x, y, (multiBlockChangePacket.zPosition * 16) + z));
				}
			} catch(IOException exception) {
				exception.printStackTrace();
			}
			break;
		}
		case 53: {
			Packet53BlockChange blockChangePacket = (Packet53BlockChange) packet;
			eventManager.sendEvent(new BlockChangeEvent(blockChangePacket.type, blockChangePacket.metadata, blockChangePacket.xPosition, blockChangePacket.yPosition, blockChangePacket.zPosition));
			break;
		}
		case 56: {
			if(bot.isMovementDisabled())
				return;
			Packet56MapChunks chunkPacket = (Packet56MapChunks) packet;
			for(int i = 0; i < chunkPacket.primaryBitmap.length; i++)
				processChunk(chunkPacket.chunkX[i], chunkPacket.chunkZ[i], chunkPacket.chunkData[i], chunkPacket.primaryBitmap[i], chunkPacket.secondaryBitmap[i], chunkPacket.skylight, true);
			break;
		}
		case 100: {
			Packet100OpenWindow openWindowPacket = (Packet100OpenWindow) packet;
			eventManager.sendEvent(new WindowOpenEvent(openWindowPacket.windowId, openWindowPacket.inventoryType, openWindowPacket.flag ? openWindowPacket.windowTitle : "", openWindowPacket.slotsCount));
			break;
		}
		case 101: {
			Packet101CloseWindow closeWindowPacket = (Packet101CloseWindow) packet;
			eventManager.sendEvent(new WindowCloseEvent(closeWindowPacket.windowId));
			break;
		}
		case 103: {
			Packet103SetSlot slotPacket = (Packet103SetSlot) packet;
			eventManager.sendEvent(new WindowSlotChangeEvent(slotPacket.windowId, slotPacket.itemSlot, slotPacket.itemStack));
			break;
		}
		case 104: {
			Packet104WindowItems itemsPacket = (Packet104WindowItems) packet;
			eventManager.sendEvent(new WindowUpdateEvent(itemsPacket.windowId, itemsPacket.itemStack));
			break;
		}
		case 132: {
			Packet132TileEntityData tileEntityPacket = (Packet132TileEntityData) packet;
			eventManager.sendEvent(new TileEntityUpdateEvent(tileEntityPacket.xPosition, tileEntityPacket.yPosition, tileEntityPacket.zPosition, tileEntityPacket.actionType, tileEntityPacket.compound));
			break;
		}
		case 130: {
			Packet130UpdateSign signPacket = (Packet130UpdateSign) packet;
			eventManager.sendEvent(new SignUpdateEvent(signPacket.x, signPacket.y, signPacket.z, signPacket.text));
			break;
		}
		case 133: {
			Packet133OpenTileEditor editPacket = (Packet133OpenTileEditor) packet;
			eventManager.sendEvent(new EditTileEntityEvent(editPacket.x, editPacket.y, editPacket.z));
			break;
		}
		case 201: {
			Packet201PlayerInfo infoPacket = (Packet201PlayerInfo) packet;
			if(infoPacket.isConnected)
				eventManager.sendEvent(new PlayerListUpdateEvent(infoPacket.playerName, infoPacket.ping));
			else
				eventManager.sendEvent(new PlayerListRemoveEvent(infoPacket.playerName));
			break;
		}
		case 252: {
			connectionHandler.sendPacket(new Packet205ClientCommand(0));
			break;
		}
		case 253: {
			handleServerAuthData((Packet253EncryptionKeyRequest) packet);
			break;
		}
		case 255: {
			Packet255KickDisconnect kickPacket = (Packet255KickDisconnect) packet;
			eventManager.sendEvent(new KickEvent(kickPacket.reason));
			break;
		}
		}
	}

	public void handleServerAuthData(Packet253EncryptionKeyRequest keyRequest) {
		String serverId = keyRequest.serverId.trim();
		PublicKey publicKey = keyRequest.publicKey;
		SecretKey secretKey = EncryptionUtil.generateSecretKey();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();

		if(!serverId.equals("-")) {
			try {
				AuthService service = bot.getAuthService();
				Session session = bot.getSession();
				String hash = new BigInteger(EncryptionUtil.encrypt(serverId, publicKey, secretKey)).toString(16);
				if(session.isValidForAuthentication() && service.isValidSession(session))
					service.authenticate(session, hash);
				else
					connectionHandler.disconnect("Session invalid!");
			} catch(NoSuchAlgorithmException | UnsupportedEncodingException exception) {
				connectionHandler.disconnect("Unable to hash: " + exception);
			} catch(AuthenticationException | IOException exception) {
				connectionHandler.disconnect("Unable to authenticate: " + exception);
			}
		}

		connectionHandler.sendPacket(new Packet252SharedKey(secretKey, publicKey, keyRequest.verifyToken));
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
		int[] yValues = new int[chunksChanged];
		byte[][] allBlocks = new byte[chunksChanged][], allMetadata = new byte[chunksChanged][], allLight = new byte[chunksChanged][], allSkylight = new byte[chunksChanged][];
		byte[] biomes = new byte[256];
		int i = 0;
		for(int y = 0; y < 16; y++) {
			if((bitmask & (1 << y)) == 0)
				continue;
			yValues[i] = y;
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

			allBlocks[i] = blocks;
			allMetadata[i] = perBlockMetadata;
			allLight[i] = perBlockLight;
			allSkylight[i] = perBlockSkylight;
			i++;
		}
		System.arraycopy(data, data.length - 256, biomes, 0, 256);
		EventManager eventManager = bot.getEventManager();
		for(i = 0; i < chunksChanged; i++) {
			ChunkLoadEvent event = new ChunkLoadEvent(x, yValues[i], z, allBlocks[i], allMetadata[i], allLight[i], allSkylight[i], biomes.clone());
			eventManager.sendEvent(event);
		}
	}

	@EventHandler
	public void onPacketReceived(PacketReceivedEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();
		if(packet instanceof Packet252SharedKey) {
			if(!handler.supportsEncryption()) {
				handler.disconnect("ConnectionHandler does not support encryption!");
				return;
			}
			if(handler.getSharedKey() == null) {
				handler.disconnect("No shared key!");
				return;
			}
			if(!handler.isDecrypting())
				handler.enableDecryption();
		} else if(packet instanceof Packet106Transaction) {
			Packet106Transaction transactionPacket = (Packet106Transaction) packet;
			bot.getEventManager().sendEvent(new WindowTransactionCompleteEvent(transactionPacket.windowId, transactionPacket.shortWindowId, transactionPacket.accepted));
			transactionPacket.accepted = true;
			handler.sendPacket(transactionPacket);
		}
	}

	@EventHandler
	public void onPacketSent(PacketSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();
		if(packet instanceof Packet252SharedKey) {
			if(!handler.supportsEncryption()) {
				handler.disconnect("ConnectionHandler does not support encryption!");
				return;
			}
			if(handler.getSharedKey() != null) {
				handler.disconnect("Shared key already installed!");
				return;
			}
			if(!handler.isEncrypting()) {
				handler.setSharedKey(((Packet252SharedKey) packet).sharedKey);
				handler.enableEncryption();
			}
		}
	}

	public static final class Provider extends ProtocolProvider {
		@Override
		public Protocol getProtocolInstance(MinecraftBot bot) {
			return new Protocol74(bot);
		}

		@Override
		public int getSupportedVersion() {
			return VERSION;
		}

		@Override
		public String getMinecraftVersion() {
			return VERSION_NAME;
		}
	}
}
