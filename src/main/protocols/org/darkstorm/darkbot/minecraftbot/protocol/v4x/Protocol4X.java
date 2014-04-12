package org.darkstorm.darkbot.minecraftbot.protocol.v4x;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.*;

import javax.crypto.SecretKey;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.ai.BlockPlaceEvent;
import org.darkstorm.darkbot.minecraftbot.auth.*;
import org.darkstorm.darkbot.minecraftbot.event.*;
import org.darkstorm.darkbot.minecraftbot.event.EventListener;
import org.darkstorm.darkbot.minecraftbot.event.io.*;
import org.darkstorm.darkbot.minecraftbot.event.protocol.client.*;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.*;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.EntitySpawnEvent.SpawnLocation;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnData;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnLocation;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.ObjectEntitySpawnEvent.ObjectSpawnData;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.ObjectEntitySpawnEvent.ThrownObjectSpawnData;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.PaintingSpawnEvent.PaintingSpawnLocation;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.PlayerEquipmentUpdateEvent.EquipmentSlot;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.RotatedEntitySpawnEvent.RotatedSpawnLocation;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.handshake.HC00PacketHandshake;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.login.client.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.login.server.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client.C15PacketClientSettings.ChatMode;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client.C15PacketClientSettings.ViewDistance;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server.S0BPacketAnimation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server.S21PacketChunkData.ChunkData;
import org.darkstorm.darkbot.minecraftbot.util.ChatColor;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public final class Protocol4X extends AbstractProtocolX implements EventListener {
	public final class LengthPacket extends AbstractPacketX implements ReadablePacket {
		private final int length;

		private byte[] data;

		protected LengthPacket(int id, int length) {
			super(id, Protocol4X.this.getState(), Direction.DOWNSTREAM);

			this.length = length;
		}

		@Override
		public void readData(DataInputStream in) throws IOException {
			byte[] data = new byte[length];
			in.readFully(data);
			this.data = data;
		}

		public byte[] getData() {
			return data;
		}
	}

	public static final int VERSION = 4;
	public static final String VERSION_NAME = "1.7.2";

	private static final double STANCE_CONSTANT = 1.62000000476837;

	private final MinecraftBot bot;
	private final Map<String, String> lang;

	public Protocol4X(MinecraftBot bot) {
		super(VERSION);
		this.bot = bot;

		Map<String, String> lang = new HashMap<>();
		try(InputStream in = getClass().getResourceAsStream("en_US.lang")) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while((line = reader.readLine()) != null) {
				int index = line.indexOf('=');
				if(index == -1)
					continue;
				String key = line.substring(0, index);
				String value = line.substring(index + 1);
				lang.put(key, value);
			}
		} catch(Exception exception) {
			lang.clear();
		}
		this.lang = Collections.unmodifiableMap(lang);

		register(State.LOGIN, LS00PacketDisconnect.class);
		register(State.LOGIN, LS01PacketEncryptionRequest.class);
		register(State.LOGIN, LS02PacketLoginSuccess.class);

		register(State.PLAY, S00PacketKeepAlive.class);
		register(State.PLAY, S01PacketJoinGame.class);
		register(State.PLAY, S02PacketChatMessage.class);
		register(State.PLAY, S03PacketTimeUpdate.class);
		register(State.PLAY, S04PacketEntityEquipment.class);
		register(State.PLAY, S05PacketSpawnLocation.class);
		register(State.PLAY, S06PacketUpdateHealth.class);
		register(State.PLAY, S07PacketRespawn.class);
		register(State.PLAY, S08PacketTeleport.class);
		register(State.PLAY, S09PacketChangeHeldItem.class);
		register(State.PLAY, S0APacketEnterBed.class);
		register(State.PLAY, S0BPacketAnimation.class);
		register(State.PLAY, S0CPacketSpawnPlayer.class);
		register(State.PLAY, S0DPacketCollectItem.class);
		register(State.PLAY, S0EPacketSpawnObject.class);
		register(State.PLAY, S0FPacketSpawnMob.class);
		register(State.PLAY, S10PacketSpawnPainting.class);
		register(State.PLAY, S11PacketSpawnExperienceOrb.class);
		register(State.PLAY, S12PacketEntityVelocityUpdate.class);
		register(State.PLAY, S13PacketDespawnEntities.class);
		register(State.PLAY, S14PacketEntityUpdate.class);
		register(State.PLAY, S15PacketEntityRelativeMovementUpdate.class);
		register(State.PLAY, S16PacketEntityRotationUpdate.class);
		register(State.PLAY, S17PacketEntityRelativeMovementRotationUpdate.class);
		register(State.PLAY, S18PacketEntityPositionRotationUpdate.class);
		register(State.PLAY, S19PacketEntityHeadRotationUpdate.class);
		register(State.PLAY, S1APacketEntityStatusUpdate.class);
		register(State.PLAY, S1BPacketEntityAttachmentUpdate.class);
		register(State.PLAY, S1CPacketEntityMetadataUpdate.class);
		register(State.PLAY, S1DPacketEntityEffectUpdate.class);
		register(State.PLAY, S1EPacketEntityRemoveEffect.class);
		register(State.PLAY, S1FPacketExperienceUpdate.class);
		register(State.PLAY, S20PacketEntityPropertyUpdate.class);
		register(State.PLAY, S21PacketChunkData.class);
		register(State.PLAY, S22PacketMultiBlockUpdate.class);
		register(State.PLAY, S23PacketBlockUpdate.class);
		register(State.PLAY, S24PacketBlockAction.class);
		register(State.PLAY, S25PacketBlockBreakAnimation.class);
		register(State.PLAY, S26MultiChunkData.class);

		register(State.PLAY, S40PacketDisconnect.class);

		bot.getEventBus().register(this);
	}

	@Override
	public Packet createPacket(PacketLengthHeader header) {
		Packet packet = super.createPacket(header);
		if(packet != null)
			return packet;
		// System.out.println("Could not find packet for " + header);
		return new LengthPacket(header.getId(), header.getLength() - AbstractPacketX.varIntLength(header.getId()));
	}

	@EventHandler
	public void onHandshake(HandshakeEvent event) {
		bot.getConnectionHandler().sendPacket(new HC00PacketHandshake(VERSION, event.getServer(), event.getPort(), State.LOGIN));
		bot.getConnectionHandler().sendPacket(new LC00PacketLoginStart(event.getSession().getUsername()));
	}

	/*@EventHandler
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
	}*/

	@EventHandler
	public void onHeldItemDrop(HeldItemDropEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		C07PacketBlockDig.Action action = event.isEntireStack() ? C07PacketBlockDig.Action.DROP_ITEM_STACK : C07PacketBlockDig.Action.DROP_ITEM;
		handler.sendPacket(new C07PacketBlockDig(action, 0, 0, 0, 0));
	}

	@EventHandler
	public void onHeldItemChange(HeldItemChangeEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C09PacketHeldItemChange(event.getNewSlot()));
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
		handler.sendPacket(new C02PacketUseEntity(event.getEntity().getId(), mode));
	}

	@EventHandler
	public void onArmSwing(ArmSwingEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C0APacketAnimation(bot.getPlayer().getId(), C0APacketAnimation.Animation.SWING_ARM));
	}

	@EventHandler
	public void onCrouchUpdate(CrouchUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C0BPacketEntityAction(bot.getPlayer().getId(), event.isCrouching() ? C0BPacketEntityAction.Action.CROUCH
				: C0BPacketEntityAction.Action.UNCROUCH, 0));
	}

	@EventHandler
	public void onSprintUpdate(SprintUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C0BPacketEntityAction(bot.getPlayer().getId(), event.isSprinting() ? C0BPacketEntityAction.Action.START_SPRINTING
				: C0BPacketEntityAction.Action.STOP_SPRINTING, 0));
	}

	@EventHandler
	public void onBedLeave(BedLeaveEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C0BPacketEntityAction(bot.getPlayer().getId(), C0BPacketEntityAction.Action.LEAVE_BED, 0));
	}

	@EventHandler
	public void onChatSent(ChatSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C01PacketChat(event.getMessage()));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		C07PacketBlockDig.Action action;
		if(event instanceof BlockBreakStartEvent)
			action = C07PacketBlockDig.Action.START_DIGGING;
		else if(event instanceof BlockBreakStopEvent)
			action = C07PacketBlockDig.Action.CANCEL_DIGGING;
		else if(event instanceof BlockBreakCompleteEvent)
			action = C07PacketBlockDig.Action.FINISH_DIGGING;
		else
			return;
		handler.sendPacket(new C07PacketBlockDig(action, event.getX(), event.getY(), event.getZ(), event.getFace()));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C08PacketBlockPlace(event.getX(), event.getY(), event.getZ(), event.getFace(), event.getItem(), event.getXOffset(), event
				.getYOffset(), event.getZOffset()));
	}

	@EventHandler
	public void onPlayerUpdate(PlayerUpdateEvent event) {
		MainPlayerEntity player = event.getEntity();
		double x = player.getX(), y = player.getY(), z = player.getZ(), yaw = player.getYaw(), pitch = player.getPitch();
		boolean move = x != player.getLastX() || y != player.getLastY() || z != player.getLastZ();
		boolean rotate = yaw != player.getLastYaw() || pitch != player.getLastPitch();
		boolean onGround = player.isOnGround();
		C03PacketPlayerUpdate packet;
		if(move && rotate)
			packet = new C06PacketPositionRotationUpdate(x, y, y + STANCE_CONSTANT, z, yaw, pitch, onGround);
		else if(move)
			packet = new C04PacketPositionUpdate(x, y, y + STANCE_CONSTANT, z, onGround);
		else if(rotate)
			packet = new C05PacketRotationUpdate(yaw, pitch, onGround);
		else
			packet = new C03PacketPlayerUpdate(onGround);
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onItemUse(ItemUseEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		ItemStack item = event.getItem();
		handler.sendPacket(new C08PacketBlockPlace(-1, -1, -1, item != null && item.getId() == 346 ? 255 : -1, item, 0, 0, 0));
	}

	@EventHandler
	public void onRequestRespawn(RequestRespawnEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new C16PacketClientStatus(1));
	}

	@EventHandler
	public void onRequestDisconnect(RequestDisconnectEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		// Ensures sending of previously queued packets now that there is no
		// disconnect packet included
		handler.sendPacket(new C01PacketChat("\n"));
	}

	@EventHandler
	public void onPacketReceived(PacketReceivedEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();
		if(packet instanceof LS01PacketEncryptionRequest) {
			handler.pauseReading();
			handleEncryption((LS01PacketEncryptionRequest) packet);
		} else if(packet instanceof LS02PacketLoginSuccess) {
			handler.pauseReading();
		}/* else if(packet instanceof Packet106Transaction) {
			Packet106Transaction transactionPacket = (Packet106Transaction) packet;
			bot.getEventManager().sendEvent(new WindowTransactionCompleteEvent(transactionPacket.windowId, transactionPacket.shortWindowId, transactionPacket.accepted));
			transactionPacket.accepted = true;
			handler.sendPacket(transactionPacket);
			}*/
	}

	@EventHandler
	public void onPacketSent(PacketSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();

		if(packet instanceof HC00PacketHandshake) {
			setState(((HC00PacketHandshake) packet).getNextState());
		} else if(packet instanceof LC01PacketEncryptionResponse) {
			if(!handler.supportsEncryption()) {
				handler.disconnect("ConnectionHandler does not support encryption!");
				return;
			}
			if(handler.getSharedKey() != null) {
				handler.disconnect("Shared key already installed!");
				return;
			}
			if(!handler.isEncrypting()) {
				handler.setSharedKey(((LC01PacketEncryptionResponse) packet).getSecretKey());
				handler.enableEncryption();
			}
			if(handler.getSharedKey() == null) {
				handler.disconnect("No shared key!");
				return;
			}
			if(!handler.isDecrypting()) {
				handler.enableDecryption();
				handler.resumeReading();
			}
		}
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		EventBus eventBus = bot.getEventBus();

		switch(getState()) {
		case LOGIN:
			switch(packet.getId()) {
			case 0x00: {
				connectionHandler.disconnect(((LS00PacketDisconnect) packet).getData());
				break;
			}
			case 0x01: {
				break;
			}
			case 0x02: {
				connectionHandler.resumeReading();
				setState(State.PLAY);
				break;
			}
			}
			break;
		case PLAY:
			switch(packet.getId()) {
			case 0x00: {
				connectionHandler.sendPacket(new C00PacketKeepAlive(((S00PacketKeepAlive) packet).getPingId()));
				break;
			}
			case 0x01: {
				S01PacketJoinGame joinPacket = (S01PacketJoinGame) packet;
				eventBus.fire(new LoginEvent(	joinPacket.getPlayerId(),
												joinPacket.getWorldType(),
												joinPacket.getGameMode(),
												joinPacket.getDimension(),
												joinPacket.getDifficulty(),
												256,
												joinPacket.getMaxPlayers()));
				connectionHandler.sendPacket(new C15PacketClientSettings("en_US", ViewDistance.FAR, ChatMode.ENABLED, Difficulty.NORMAL, true, true));
				break;
			}
			case 0x02: {
				S02PacketChatMessage chatPacket = (S02PacketChatMessage) packet;
				String message = chatPacket.getMessage();
				try {
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(message);

					String text = parseChatMessage(json);
					System.out.println("Parsing chat message: " + message + " -> " + text);
					eventBus.fire(new ChatReceivedEvent(text));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				break;
			}
			case 0x03: {
				S03PacketTimeUpdate timePacket = (S03PacketTimeUpdate) packet;
				eventBus.fire(new TimeUpdateEvent(timePacket.getTime(), timePacket.getWorldAge()));
				break;
			}
			case 0x04: {
				S04PacketEntityEquipment equipmentPacket = (S04PacketEntityEquipment) packet;
				eventBus.fire(new PlayerEquipmentUpdateEvent(	equipmentPacket.getEntityId(),
																EquipmentSlot.fromId(equipmentPacket.getSlot().ordinal()),
																equipmentPacket.getItem()));
				break;
			}
			case 0x06: {
				S06PacketUpdateHealth healthPacket = (S06PacketUpdateHealth) packet;
				int health = (int) Math.ceil(healthPacket.getHealth());
				eventBus.fire(new HealthUpdateEvent(health, healthPacket.getFood(), (float) healthPacket.getFoodSaturation()));
				break;
			}
			case 0x07: {
				S07PacketRespawn respawnPacket = (S07PacketRespawn) packet;
				eventBus.fire(new RespawnEvent(respawnPacket.getDimension(), respawnPacket.getDifficulty(), respawnPacket.getGameMode(), respawnPacket
						.getWorldType(), 256));
				break;
			}
			case 0x08: {
				S08PacketTeleport teleportPacket = (S08PacketTeleport) packet;

				C06PacketPositionRotationUpdate clientUpdatePacket = new C06PacketPositionRotationUpdate(	teleportPacket.getX(),
																											teleportPacket.getY(),
																											teleportPacket.getZ(),
																											teleportPacket.getY() + STANCE_CONSTANT,
																											teleportPacket.getYaw(),
																											teleportPacket.getPitch(),
																											teleportPacket.isGrounded());
				connectionHandler.sendPacket(clientUpdatePacket);

				eventBus.fire(new TeleportEvent(teleportPacket.getX(),
												teleportPacket.getY(),
												teleportPacket.getZ(),
												(float) teleportPacket.getYaw(),
												(float) teleportPacket.getPitch()));
				break;
			}
			case 0x09: {
				S09PacketChangeHeldItem heldItemPacket = (S09PacketChangeHeldItem) packet;
				eventBus.fire(new org.darkstorm.darkbot.minecraftbot.event.protocol.server.ChangeHeldItemEvent(heldItemPacket.getSlot()));
				break;
			}
			case 0x0A: {
				S0APacketEnterBed sleepPacket = (S0APacketEnterBed) packet;
				eventBus.fire(new SleepEvent(sleepPacket.getEntityId(), sleepPacket.getBedX(), sleepPacket.getBedY(), sleepPacket.getBedZ()));
				break;
			}
			case 0x0B: {
				S0BPacketAnimation animationPacket = (S0BPacketAnimation) packet;
				if(animationPacket.getAnimation() == Animation.EAT_FOOD)
					eventBus.fire(new EntityEatEvent(animationPacket.getPlayerId()));
				break;
			}
			case 0x0C: {
				S0CPacketSpawnPlayer spawnPacket = (S0CPacketSpawnPlayer) packet;
				RotatedSpawnLocation location = new RotatedSpawnLocation(	spawnPacket.getX(),
																			spawnPacket.getY(),
																			spawnPacket.getZ(),
																			spawnPacket.getYaw(),
																			spawnPacket.getPitch());
				ItemStack heldItem = new BasicItemStack(spawnPacket.getHeldItemId(), 1, 0);
				eventBus.fire(new PlayerSpawnEvent(spawnPacket.getEntityId(), spawnPacket.getName(), heldItem, location, spawnPacket.getMetadata()));
				break;
			}
			case 0x0D: {
				S0DPacketCollectItem collectPacket = (S0DPacketCollectItem) packet;
				eventBus.fire(new EntityCollectEvent(collectPacket.getItemEntityId(), collectPacket.getCollectorEntityId()));
				break;
			}
			case 0x0E: {
				S0EPacketSpawnObject spawnPacket = (S0EPacketSpawnObject) packet;
				RotatedSpawnLocation location = new RotatedSpawnLocation(	spawnPacket.getX(),
																			spawnPacket.getY(),
																			spawnPacket.getZ(),
																			spawnPacket.getYaw(),
																			spawnPacket.getPitch());
				ObjectSpawnData spawnData;
				if(spawnPacket.getData() != 0)
					spawnData = new ThrownObjectSpawnData(	spawnPacket.getType(),
															spawnPacket.getData(),
															spawnPacket.getVelocityX(),
															spawnPacket.getVelocityY(),
															spawnPacket.getVelocityZ());
				else
					spawnData = new ObjectSpawnData(spawnPacket.getType());
				eventBus.fire(new ObjectEntitySpawnEvent(spawnPacket.getEntityId(), location, spawnData));
				break;
			}
			case 0x0F: {
				S0FPacketSpawnMob spawnPacket = (S0FPacketSpawnMob) packet;
				LivingEntitySpawnLocation location = new LivingEntitySpawnLocation(	spawnPacket.getX(),
																					spawnPacket.getY(),
																					spawnPacket.getZ(),
																					spawnPacket.getYaw(),
																					spawnPacket.getPitch(),
																					spawnPacket.getHeadYaw());
				LivingEntitySpawnData data = new LivingEntitySpawnData(	spawnPacket.getType(),
																		spawnPacket.getVelocityX(),
																		spawnPacket.getVelocityY(),
																		spawnPacket.getVelocityZ());
				eventBus.fire(new LivingEntitySpawnEvent(spawnPacket.getEntityId(), location, data, spawnPacket.getMetadata()));
				break;
			}
			case 0x10: {
				S10PacketSpawnPainting spawnPacket = (S10PacketSpawnPainting) packet;
				PaintingSpawnLocation location = new PaintingSpawnLocation(spawnPacket.getX(), spawnPacket.getY(), spawnPacket.getZ(), spawnPacket.getFace());
				eventBus.fire(new PaintingSpawnEvent(spawnPacket.getEntityId(), location, spawnPacket.getTitle()));
				break;
			}
			case 0x11: {
				S11PacketSpawnExperienceOrb spawnPacket = (S11PacketSpawnExperienceOrb) packet;
				SpawnLocation location = new SpawnLocation(spawnPacket.getX(), spawnPacket.getY(), spawnPacket.getZ());
				eventBus.fire(new ExpOrbSpawnEvent(spawnPacket.getEntityId(), location, spawnPacket.getCount()));
				break;
			}
			case 0x12: {
				S12PacketEntityVelocityUpdate velocityPacket = (S12PacketEntityVelocityUpdate) packet;
				eventBus.fire(new EntityVelocityEvent(	velocityPacket.getEntityId(),
														velocityPacket.getVelocityX(),
														velocityPacket.getVelocityY(),
														velocityPacket.getVelocityZ()));
				break;
			}
			case 0x13: {
				S13PacketDespawnEntities despawnPacket = (S13PacketDespawnEntities) packet;
				for(int id : despawnPacket.getEntityIds())
					eventBus.fire(new EntityDespawnEvent(id));
				break;
			}
			case 0x15: {
				S15PacketEntityRelativeMovementUpdate updatePacket = (S15PacketEntityRelativeMovementUpdate) packet;
				eventBus.fire(new EntityMoveEvent(updatePacket.getEntityId(), updatePacket.getDX(), updatePacket.getDY(), updatePacket.getDZ()));
				break;
			}
			case 0x16: {
				S16PacketEntityRotationUpdate updatePacket = (S16PacketEntityRotationUpdate) packet;
				eventBus.fire(new EntityRotateEvent(updatePacket.getEntityId(), updatePacket.getYaw(), updatePacket.getPitch()));
				break;
			}
			case 0x17: {
				S17PacketEntityRelativeMovementRotationUpdate updatePacket = (S17PacketEntityRelativeMovementRotationUpdate) packet;
				eventBus.fire(new EntityMoveEvent(updatePacket.getEntityId(), updatePacket.getDX(), updatePacket.getDY(), updatePacket.getDZ()));
				eventBus.fire(new EntityRotateEvent(updatePacket.getEntityId(), updatePacket.getYaw(), updatePacket.getPitch()));
				break;
			}
			case 0x18: {
				S18PacketEntityPositionRotationUpdate updatePacket = (S18PacketEntityPositionRotationUpdate) packet;
				eventBus.fire(new EntityTeleportEvent(updatePacket.getEntityId(), updatePacket.getX(), updatePacket.getY(), updatePacket.getZ(), updatePacket
						.getYaw(), updatePacket.getPitch()));
				break;
			}
			case 0x19: {
				S19PacketEntityHeadRotationUpdate updatePacket = (S19PacketEntityHeadRotationUpdate) packet;
				eventBus.fire(new EntityHeadRotateEvent(updatePacket.getEntityId(), updatePacket.getHeadYaw()));
				break;
			}
			case 0x1A: {
				S1APacketEntityStatusUpdate updatePacket = (S1APacketEntityStatusUpdate) packet;
				if(updatePacket.getStatus() == 2)
					eventBus.fire(new EntityHurtEvent(updatePacket.getEntityId()));
				else if(updatePacket.getStatus() == 3)
					eventBus.fire(new EntityDeathEvent(updatePacket.getEntityId()));
				else if(updatePacket.getStatus() == 9)
					eventBus.fire(new EntityStopEatingEvent(updatePacket.getEntityId()));
				break;
			}
			case 0x1B: {
				S1BPacketEntityAttachmentUpdate updatePacket = (S1BPacketEntityAttachmentUpdate) packet;
				if(updatePacket.isWithLeash())
					break;
				if(updatePacket.getAttachedEntityId() != -1)
					eventBus.fire(new EntityMountEvent(updatePacket.getEntityId(), updatePacket.getAttachedEntityId()));
				else
					eventBus.fire(new EntityDismountEvent(updatePacket.getEntityId()));
				break;
			}
			case 0x1C: {
				S1CPacketEntityMetadataUpdate updatePacket = (S1CPacketEntityMetadataUpdate) packet;
				eventBus.fire(new EntityMetadataUpdateEvent(updatePacket.getEntityId(), updatePacket.getMetadata()));
				break;
			}
			case 0x21: {
				if(bot.isMovementDisabled())
					return;
				S21PacketChunkData chunkPacket = (S21PacketChunkData) packet;
				processChunk(chunkPacket.getChunk(), bot.getWorld().getDimension() == Dimension.OVERWORLD, chunkPacket.hasBiomes());
				break;
			}
			case 0x22: {
				S22PacketMultiBlockUpdate blockPacket = (S22PacketMultiBlockUpdate) packet;
				if(blockPacket.getBlockData() == null)
					return;
				int chunkX = blockPacket.getX() * 16, chunkZ = blockPacket.getZ() * 16;
				int[] blockData = blockPacket.getBlockData();
				for(int i = 0; i < blockData.length; i++) {
					int data = blockData[i];

					int id = (data >> 4) & 0xFFF;
					int metadata = data & 0xF;

					int x = chunkX + (data >> 28) & 0xF;
					int y = (data >> 16) & 0xFF;
					int z = chunkZ + (data >> 24) & 0xF;

					eventBus.fire(new BlockChangeEvent(id, metadata, x, y, z));
				}
				break;
			}
			case 0x23: {
				S23PacketBlockUpdate blockPacket = (S23PacketBlockUpdate) packet;
				eventBus.fire(new BlockChangeEvent(	blockPacket.getBlockId(),
													blockPacket.getBlockMetadata(),
													blockPacket.getX(),
													blockPacket.getY(),
													blockPacket.getZ()));
				break;
			}
			case 0x26: {
				if(bot.isMovementDisabled())
					return;
				S26MultiChunkData chunkPacket = (S26MultiChunkData) packet;
				for(ChunkData chunk : chunkPacket.getChunks())
					processChunk(chunk, chunkPacket.hasSkylight(), true);
				break;
			}
			}
			break;
		default:
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handleEncryption(LS01PacketEncryptionRequest request) {
		String serverId = request.getServerId().trim();
		PublicKey publicKey;
		try {
			publicKey = EncryptionUtil.generatePublicKey(request.getPublicKey());
		} catch(GeneralSecurityException exception) {
			throw new Error("Unable to generate public key", exception);
		}
		SecretKey secretKey = EncryptionUtil.generateSecretKey();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();

		if(!serverId.equals("-")) {
			try {
				AuthService service = bot.getAuthService();
				Session session = bot.getSession();

				String hash = new BigInteger(EncryptionUtil.encrypt(serverId, publicKey, secretKey)).toString(16);
				service.authenticate(service.validateSession(session), hash, bot.getLoginProxy());
			} catch(InvalidSessionException exception) {
				connectionHandler.disconnect("Session invalid: " + exception);
			} catch(NoSuchAlgorithmException | UnsupportedEncodingException exception) {
				connectionHandler.disconnect("Unable to hash: " + exception);
			} catch(AuthenticationException | IOException exception) {
				connectionHandler.disconnect("Unable to authenticate: " + exception);
			}
		}

		connectionHandler.sendPacket(new LC01PacketEncryptionResponse(secretKey, publicKey, request.getVerifyToken()));
	}

	public String parseChatMessage(JSONObject messageData) {
		StringBuffer message = new StringBuffer();
		if(messageData.get("bold") == Boolean.TRUE)
			message.append(ChatColor.BOLD);
		if(messageData.get("italic") == Boolean.TRUE)
			message.append(ChatColor.ITALIC);
		if(messageData.get("underlined") == Boolean.TRUE)
			message.append(ChatColor.UNDERLINE);
		if(messageData.get("strikethrough") == Boolean.TRUE)
			message.append(ChatColor.STRIKETHROUGH);
		if(messageData.get("obfuscated") == Boolean.TRUE)
			message.append(ChatColor.OBFUSCATED);
		if(messageData.containsKey("color"))
			message.append(ChatColor.valueOf(messageData.get("color").toString().toUpperCase()));

		if(messageData.containsKey("translate")) {
			String key = (String) messageData.get("translate");

			String text = lang.get(key);
			if(text == null)
				text = key;

			if(messageData.containsKey("with")) {
				JSONArray array = (JSONArray) messageData.get("with");
				String[] translationValues = new String[array.size()];
				for(int i = 0; i < translationValues.length; i++) {
					Object object = array.get(i);

					String value;
					if(object instanceof JSONObject)
						value = parseChatMessage((JSONObject) object);
					else
						value = (String) object;
					translationValues[i] = value;
				}

				text = String.format(text, (Object[]) translationValues);
			}

			message.append(text);
		} else if(messageData.containsKey("text"))
			message.append(messageData.get("text"));

		if(messageData.containsKey("extra")) {
			JSONArray extra = (JSONArray) messageData.get("extra");
			for(Object object : extra)
				if(object instanceof JSONObject)
					message.append(parseChatMessage((JSONObject) object));
				else
					message.append(object);
		}

		return message.toString();
	}

	private void processChunk(ChunkData chunk, boolean addSkylight, boolean addBiomes) {
		if(chunk == null)
			return;
		int x = chunk.getX(), z = chunk.getZ();
		int bitmask = chunk.getPrimaryBitmask();
		byte[] data = chunk.getData();

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
		EventBus eventBus = bot.getEventBus();
		for(i = 0; i < chunksChanged; i++) {
			ChunkLoadEvent event = new ChunkLoadEvent(x, yValues[i], z, allBlocks[i], allMetadata[i], allLight[i], allSkylight[i], biomes.clone());
			eventBus.fire(event);
		}
	}

	public static final class Provider extends ProtocolProvider<Protocol4X> {
		@Override
		public Protocol4X getProtocolInstance(MinecraftBot bot) {
			return new Protocol4X(bot);
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
