package org.darkstorm.darkbot.minecraftbot.protocol.v4x;

import java.io.*;
import java.math.*;
import java.security.*;
import java.util.*;

import javax.crypto.SecretKey;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
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
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.handshake.PacketHC00_Handshake;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.login.client.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.login.server.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client.PacketC15_ClientSettings.ChatMode;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client.PacketC15_ClientSettings.ViewDistance;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server.*;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server.PacketS0B_Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server.PacketS21_ChunkData.ChunkData;
import org.darkstorm.darkbot.minecraftbot.util.ChatColor;
import org.darkstorm.darkbot.minecraftbot.world.*;
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

	private static final double STANCE_CONSTANT = 1.620000004768372;
	private static final BigDecimal STANCE_CONSTANT_PRECISE = BigDecimal.valueOf(STANCE_CONSTANT);

	private final MinecraftBot bot;
	private final Map<String, String> lang;

	private boolean positionSet = false;

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

		register(State.LOGIN, PacketLS00_Disconnect.class);
		register(State.LOGIN, PacketLS01_EncryptionRequest.class);
		register(State.LOGIN, PacketLS02_LoginSuccess.class);

		register(State.PLAY, PacketS00_KeepAlive.class);
		register(State.PLAY, PacketS01_JoinGame.class);
		register(State.PLAY, PacketS02_ChatMessage.class);
		register(State.PLAY, PacketS03_TimeUpdate.class);
		register(State.PLAY, PacketS04_EntityEquipment.class);
		register(State.PLAY, PacketS05_SpawnLocation.class);
		register(State.PLAY, PacketS06_UpdateHealth.class);
		register(State.PLAY, PacketS07_Respawn.class);
		register(State.PLAY, PacketS08_Teleport.class);
		register(State.PLAY, PacketS09_ChangeHeldItem.class);
		register(State.PLAY, PacketS0A_EnterBed.class);
		register(State.PLAY, PacketS0B_Animation.class);
		register(State.PLAY, PacketS0C_SpawnPlayer.class);
		register(State.PLAY, PacketS0D_CollectItem.class);
		register(State.PLAY, PacketS0E_SpawnObject.class);
		register(State.PLAY, PacketS0F_SpawnMob.class);
		register(State.PLAY, PacketS10_SpawnPainting.class);
		register(State.PLAY, PacketS11_SpawnExperienceOrb.class);
		register(State.PLAY, PacketS12_EntityVelocityUpdate.class);
		register(State.PLAY, PacketS13_DespawnEntities.class);
		register(State.PLAY, PacketS14_EntityUpdate.class);
		register(State.PLAY, PacketS15_EntityRelativeMovementUpdate.class);
		register(State.PLAY, PacketS16_EntityRotationUpdate.class);
		register(State.PLAY, PacketS17_EntityRelativeMovementRotationUpdate.class);
		register(State.PLAY, PacketS18_EntityPositionRotationUpdate.class);
		register(State.PLAY, PacketS19_EntityHeadRotationUpdate.class);
		register(State.PLAY, PacketS1A_EntityStatusUpdate.class);
		register(State.PLAY, PacketS1B_EntityAttachmentUpdate.class);
		register(State.PLAY, PacketS1C_EntityMetadataUpdate.class);
		register(State.PLAY, PacketS1D_EntityEffectUpdate.class);
		register(State.PLAY, PacketS1E_EntityRemoveEffect.class);
		register(State.PLAY, PacketS1F_ExperienceUpdate.class);
		register(State.PLAY, PacketS20_EntityPropertyUpdate.class);
		register(State.PLAY, PacketS21_ChunkData.class);
		register(State.PLAY, PacketS22_MultiBlockUpdate.class);
		register(State.PLAY, PacketS23_BlockUpdate.class);
		register(State.PLAY, PacketS24_BlockAction.class);
		register(State.PLAY, PacketS25_BlockBreakAnimation.class);
		register(State.PLAY, PacketS26_MultiChunkData.class);

		register(State.PLAY, PacketS2D_OpenWindow.class);
		register(State.PLAY, PacketS2E_CloseWindow.class);
		register(State.PLAY, PacketS2F_SetSlot.class);
		register(State.PLAY, PacketS30_WindowItems.class);
		register(State.PLAY, PacketS31_WindowProperty.class);
		register(State.PLAY, PacketS32_ConfirmTransaction.class);
		register(State.PLAY, PacketS33_SignUpdate.class);
		register(State.PLAY, PacketS34_MapUpdate.class);
		register(State.PLAY, PacketS35_TileEntityUpdate.class);
		register(State.PLAY, PacketS36_OpenTileEditor.class);
		register(State.PLAY, PacketS37_Statistics.class);
		register(State.PLAY, PacketS38_PlayerListItem.class);
		register(State.PLAY, PacketS39_PlayerAbilities.class);
		register(State.PLAY, PacketS3A_TabCompletion.class);

		register(State.PLAY, PacketS40_Disconnect.class);

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
		bot.getConnectionHandler().sendPacket(new PacketHC00_Handshake(VERSION, event.getServer(), event.getPort(), State.LOGIN));
		bot.getConnectionHandler().sendPacket(new PacketLC00_LoginStart(event.getSession().getUsername()));
	}

	@EventHandler
	public void onInventoryChange(InventoryChangeEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		int windowId = event.getInventory().getWindowId();
		int mode = event.isShiftHeld() ? 1 : 0;
		PacketC0E_ClickWindow packet = new PacketC0E_ClickWindow(windowId, event.getSlot(), event.getButton(), event.getTransactionId(), mode, event.getItem());
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC0D_CloseWindow(event.getInventory().getWindowId()));
	}

	@EventHandler
	public void onHeldItemDrop(HeldItemDropEvent event) {
		if(positionSet) {
			ConnectionHandler handler = bot.getConnectionHandler();
			PacketC07_BlockDig.Action action = event.isEntireStack() ? PacketC07_BlockDig.Action.DROP_ITEM_STACK : PacketC07_BlockDig.Action.DROP_ITEM;
			handler.sendPacket(new PacketC07_BlockDig(action, 0, 0, 0, 0));
		} else
			event.setCancelled(true);
	}

	@EventHandler
	public void onHeldItemChange(HeldItemChangeEvent event) {
		if(positionSet) {
			ConnectionHandler handler = bot.getConnectionHandler();
			handler.sendPacket(new PacketC09_HeldItemChange(event.getNewSlot()));
		} else
			event.setCancelled(true);
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
		handler.sendPacket(new PacketC02_UseEntity(event.getEntity().getId(), mode));
	}

	@EventHandler
	public void onArmSwing(ArmSwingEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC0A_Animation(bot.getPlayer().getId(), PacketC0A_Animation.Animation.SWING_ARM));
	}

	@EventHandler
	public void onCrouchUpdate(CrouchUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC0B_EntityAction(bot.getPlayer().getId(), event.isCrouching() ? PacketC0B_EntityAction.Action.CROUCH
				: PacketC0B_EntityAction.Action.UNCROUCH, 0));
	}

	@EventHandler
	public void onSprintUpdate(SprintUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC0B_EntityAction(bot.getPlayer().getId(), event.isSprinting() ? PacketC0B_EntityAction.Action.START_SPRINTING
				: PacketC0B_EntityAction.Action.STOP_SPRINTING, 0));
	}

	@EventHandler
	public void onBedLeave(BedLeaveEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC0B_EntityAction(bot.getPlayer().getId(), PacketC0B_EntityAction.Action.LEAVE_BED, 0));
	}

	@EventHandler
	public void onChatSent(ChatSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC01_Chat(event.getMessage()));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		PacketC07_BlockDig.Action action;
		if(event instanceof BlockBreakStartEvent)
			action = PacketC07_BlockDig.Action.START_DIGGING;
		else if(event instanceof BlockBreakStopEvent)
			action = PacketC07_BlockDig.Action.CANCEL_DIGGING;
		else if(event instanceof BlockBreakCompleteEvent)
			action = PacketC07_BlockDig.Action.FINISH_DIGGING;
		else
			return;
		handler.sendPacket(new PacketC07_BlockDig(action, event.getX(), event.getY(), event.getZ(), event.getFace()));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC08_BlockPlace(event.getX(), event.getY(), event.getZ(), event.getFace(), event.getItem(), event.getXOffset(), event
				.getYOffset(), event.getZOffset()));
	}

	@EventHandler
	public void onPlayerUpdate(PlayerUpdateEvent event) {
		double x = event.getX(), y = event.getY(), z = event.getZ(), yaw = event.getYaw(), pitch = event.getPitch();
		double eyeY = BigDecimal.valueOf(y).add(STANCE_CONSTANT_PRECISE).doubleValue();
		boolean onGround = event.isOnGround();
		PacketC03_PlayerUpdate packet;
		if(event instanceof PlayerMoveRotateEvent)
			packet = new PacketC06_PositionRotationUpdate(x, y, z, eyeY, yaw, pitch, onGround);
		else if(event instanceof PlayerMoveEvent)
			packet = new PacketC04_PositionUpdate(x, y, z, eyeY, onGround);
		else if(event instanceof PlayerRotateEvent)
			packet = new PacketC05_RotationUpdate(yaw, pitch, onGround);
		else
			packet = new PacketC03_PlayerUpdate(onGround);
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onItemUse(ItemUseEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		ItemStack item = event.getItem();
		handler.sendPacket(new PacketC08_BlockPlace(-1, -1, -1, item != null && item.getId() == 346 ? 255 : -1, item, 0, 0, 0));
	}

	@EventHandler
	public void onRequestRespawn(RequestRespawnEvent event) {
		System.out.println("Respawn requested! :o");
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new PacketC16_ClientStatus(0));
	}

	@EventHandler
	public void onRequestDisconnect(RequestDisconnectEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		// Ensures sending of previously queued packets now that there is no
		// disconnect packet included
		handler.sendPacket(new PacketC04_PositionUpdate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, true));
	}

	@EventHandler
	public void onPacketReceived(PacketReceivedEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();
		if(packet instanceof PacketLS01_EncryptionRequest) {
			handler.pauseReading();
			handleEncryption((PacketLS01_EncryptionRequest) packet);
		} else if(packet instanceof PacketLS02_LoginSuccess) {
			handler.pauseReading();
		} else if(packet instanceof PacketS32_ConfirmTransaction) {
			PacketS32_ConfirmTransaction transactionPacket = (PacketS32_ConfirmTransaction) packet;
			handler.sendPacket(new PacketC0F_ConfirmTransaction(transactionPacket.getWindowId(), transactionPacket.getActionId(), true));
			bot.getEventBus().fire(new WindowTransactionCompleteEvent(	transactionPacket.getWindowId(),
																		(short) transactionPacket.getActionId(),
																		transactionPacket.isAccepted()));
		}
	}

	@EventHandler
	public void onPacketSent(PacketSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();

		if(packet instanceof PacketHC00_Handshake) {
			setState(((PacketHC00_Handshake) packet).getNextState());
		} else if(packet instanceof PacketLC01_EncryptionResponse) {
			if(!handler.supportsEncryption()) {
				handler.disconnect("ConnectionHandler does not support encryption!");
				return;
			}
			if(handler.getSharedKey() != null) {
				handler.disconnect("Shared key already installed!");
				return;
			}
			if(!handler.isEncrypting()) {
				handler.setSharedKey(((PacketLC01_EncryptionResponse) packet).getSecretKey());
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
				connectionHandler.disconnect(((PacketLS00_Disconnect) packet).getData());
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
				connectionHandler.sendPacket(new PacketC00_KeepAlive(((PacketS00_KeepAlive) packet).getPingId()));
				break;
			}
			case 0x01: {
				PacketS01_JoinGame joinPacket = (PacketS01_JoinGame) packet;
				eventBus.fire(new LoginEvent(	joinPacket.getPlayerId(),
												joinPacket.getWorldType(),
												joinPacket.getGameMode(),
												joinPacket.getDimension(),
												joinPacket.getDifficulty(),
												256,
												joinPacket.getMaxPlayers()));
				connectionHandler.sendPacket(new PacketC15_ClientSettings("en_US", ViewDistance.FAR, ChatMode.ENABLED, Difficulty.NORMAL, true, true));
				try {
					connectionHandler.sendPacket(new PacketC17_PluginMessage("MC|Brand", "vanilla".getBytes("UTF-8")));
				} catch(UnsupportedEncodingException exception) {
					throw new RuntimeException(exception);
				}
				break;
			}
			case 0x02: {
				PacketS02_ChatMessage chatPacket = (PacketS02_ChatMessage) packet;
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
				PacketS03_TimeUpdate timePacket = (PacketS03_TimeUpdate) packet;
				eventBus.fire(new TimeUpdateEvent(timePacket.getTime(), timePacket.getWorldAge()));
				break;
			}
			case 0x04: {
				PacketS04_EntityEquipment equipmentPacket = (PacketS04_EntityEquipment) packet;
				eventBus.fire(new PlayerEquipmentUpdateEvent(	equipmentPacket.getEntityId(),
																EquipmentSlot.fromId(equipmentPacket.getSlot().ordinal()),
																equipmentPacket.getItem()));
				break;
			}
			case 0x06: {
				PacketS06_UpdateHealth healthPacket = (PacketS06_UpdateHealth) packet;
				int health = (int) Math.ceil(healthPacket.getHealth());
				System.out.println("Received health: " + healthPacket.getHealth() + " " + healthPacket.getFood() + " " + healthPacket.getFoodSaturation());
				eventBus.fire(new HealthUpdateEvent(health, healthPacket.getFood(), (float) healthPacket.getFoodSaturation()));
				break;
			}
			case 0x07: {
				PacketS07_Respawn respawnPacket = (PacketS07_Respawn) packet;
				eventBus.fire(new RespawnEvent(respawnPacket.getDimension(), respawnPacket.getDifficulty(), respawnPacket.getGameMode(), respawnPacket
						.getWorldType(), 256));
				break;
			}
			case 0x08: {
				PacketS08_Teleport teleportPacket = (PacketS08_Teleport) packet;

				double x = teleportPacket.getX(), eyeY = teleportPacket.getY(), z = teleportPacket.getZ();
				double yaw = teleportPacket.getYaw(), pitch = teleportPacket.getPitch();
				boolean grounded = teleportPacket.isGrounded();
				// Computers can't arithmetic
				double actualY = BigDecimal.valueOf(eyeY).subtract(STANCE_CONSTANT_PRECISE).doubleValue();

				PacketC06_PositionRotationUpdate clientUpdatePacket = new PacketC06_PositionRotationUpdate(x, actualY, z, eyeY, yaw, pitch, grounded);
				connectionHandler.sendPacket(clientUpdatePacket);
				positionSet = true;

				eventBus.fire(new TeleportEvent(x, actualY, z, (float) yaw, (float) pitch));
				break;
			}
			case 0x09: {
				PacketS09_ChangeHeldItem heldItemPacket = (PacketS09_ChangeHeldItem) packet;
				eventBus.fire(new org.darkstorm.darkbot.minecraftbot.event.protocol.server.ChangeHeldItemEvent(heldItemPacket.getSlot()));
				break;
			}
			case 0x0A: {
				PacketS0A_EnterBed sleepPacket = (PacketS0A_EnterBed) packet;
				eventBus.fire(new SleepEvent(sleepPacket.getEntityId(), sleepPacket.getBedX(), sleepPacket.getBedY(), sleepPacket.getBedZ()));
				break;
			}
			case 0x0B: {
				PacketS0B_Animation animationPacket = (PacketS0B_Animation) packet;
				if(animationPacket.getAnimation() == Animation.EAT_FOOD)
					eventBus.fire(new EntityEatEvent(animationPacket.getPlayerId()));
				break;
			}
			case 0x0C: {
				PacketS0C_SpawnPlayer spawnPacket = (PacketS0C_SpawnPlayer) packet;
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
				PacketS0D_CollectItem collectPacket = (PacketS0D_CollectItem) packet;
				eventBus.fire(new EntityCollectEvent(collectPacket.getItemEntityId(), collectPacket.getCollectorEntityId()));
				break;
			}
			case 0x0E: {
				PacketS0E_SpawnObject spawnPacket = (PacketS0E_SpawnObject) packet;
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
				PacketS0F_SpawnMob spawnPacket = (PacketS0F_SpawnMob) packet;
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
				PacketS10_SpawnPainting spawnPacket = (PacketS10_SpawnPainting) packet;
				PaintingSpawnLocation location = new PaintingSpawnLocation(spawnPacket.getX(), spawnPacket.getY(), spawnPacket.getZ(), spawnPacket.getFace());
				eventBus.fire(new PaintingSpawnEvent(spawnPacket.getEntityId(), location, spawnPacket.getTitle()));
				break;
			}
			case 0x11: {
				PacketS11_SpawnExperienceOrb spawnPacket = (PacketS11_SpawnExperienceOrb) packet;
				SpawnLocation location = new SpawnLocation(spawnPacket.getX(), spawnPacket.getY(), spawnPacket.getZ());
				eventBus.fire(new ExpOrbSpawnEvent(spawnPacket.getEntityId(), location, spawnPacket.getCount()));
				break;
			}
			case 0x12: {
				PacketS12_EntityVelocityUpdate velocityPacket = (PacketS12_EntityVelocityUpdate) packet;
				eventBus.fire(new EntityVelocityEvent(	velocityPacket.getEntityId(),
														velocityPacket.getVelocityX(),
														velocityPacket.getVelocityY(),
														velocityPacket.getVelocityZ()));
				break;
			}
			case 0x13: {
				PacketS13_DespawnEntities despawnPacket = (PacketS13_DespawnEntities) packet;
				for(int id : despawnPacket.getEntityIds())
					eventBus.fire(new EntityDespawnEvent(id));
				break;
			}
			case 0x15: {
				PacketS15_EntityRelativeMovementUpdate updatePacket = (PacketS15_EntityRelativeMovementUpdate) packet;
				eventBus.fire(new EntityMoveEvent(updatePacket.getEntityId(), updatePacket.getDX(), updatePacket.getDY(), updatePacket.getDZ()));
				break;
			}
			case 0x16: {
				PacketS16_EntityRotationUpdate updatePacket = (PacketS16_EntityRotationUpdate) packet;
				eventBus.fire(new EntityRotateEvent(updatePacket.getEntityId(), updatePacket.getYaw(), updatePacket.getPitch()));
				break;
			}
			case 0x17: {
				PacketS17_EntityRelativeMovementRotationUpdate updatePacket = (PacketS17_EntityRelativeMovementRotationUpdate) packet;
				eventBus.fire(new EntityMoveEvent(updatePacket.getEntityId(), updatePacket.getDX(), updatePacket.getDY(), updatePacket.getDZ()));
				eventBus.fire(new EntityRotateEvent(updatePacket.getEntityId(), updatePacket.getYaw(), updatePacket.getPitch()));
				break;
			}
			case 0x18: {
				PacketS18_EntityPositionRotationUpdate updatePacket = (PacketS18_EntityPositionRotationUpdate) packet;
				eventBus.fire(new EntityTeleportEvent(updatePacket.getEntityId(), updatePacket.getX(), updatePacket.getY(), updatePacket.getZ(), updatePacket
						.getYaw(), updatePacket.getPitch()));
				break;
			}
			case 0x19: {
				PacketS19_EntityHeadRotationUpdate updatePacket = (PacketS19_EntityHeadRotationUpdate) packet;
				eventBus.fire(new EntityHeadRotateEvent(updatePacket.getEntityId(), updatePacket.getHeadYaw()));
				break;
			}
			case 0x1A: {
				PacketS1A_EntityStatusUpdate updatePacket = (PacketS1A_EntityStatusUpdate) packet;
				if(updatePacket.getStatus() == 2)
					eventBus.fire(new EntityHurtEvent(updatePacket.getEntityId()));
				else if(updatePacket.getStatus() == 3)
					eventBus.fire(new EntityDeathEvent(updatePacket.getEntityId()));
				else if(updatePacket.getStatus() == 9)
					eventBus.fire(new EntityStopEatingEvent(updatePacket.getEntityId()));
				break;
			}
			case 0x1B: {
				PacketS1B_EntityAttachmentUpdate updatePacket = (PacketS1B_EntityAttachmentUpdate) packet;
				if(updatePacket.isWithLeash())
					break;
				if(updatePacket.getAttachedEntityId() != -1)
					eventBus.fire(new EntityMountEvent(updatePacket.getEntityId(), updatePacket.getAttachedEntityId()));
				else
					eventBus.fire(new EntityDismountEvent(updatePacket.getEntityId()));
				break;
			}
			case 0x1C: {
				PacketS1C_EntityMetadataUpdate updatePacket = (PacketS1C_EntityMetadataUpdate) packet;
				eventBus.fire(new EntityMetadataUpdateEvent(updatePacket.getEntityId(), updatePacket.getMetadata()));
				break;
			}
			case 0x21: {
				if(bot.isMovementDisabled())
					return;
				PacketS21_ChunkData chunkPacket = (PacketS21_ChunkData) packet;
				processChunk(chunkPacket.getChunk(), bot.getWorld().getDimension() == Dimension.OVERWORLD, chunkPacket.hasBiomes());
				break;
			}
			case 0x22: {
				PacketS22_MultiBlockUpdate blockPacket = (PacketS22_MultiBlockUpdate) packet;
				if(blockPacket.getBlockData() == null)
					return;
				int chunkX = blockPacket.getX() * 16, chunkZ = blockPacket.getZ() * 16;
				int[] blockData = blockPacket.getBlockData();
				for(int i = 0; i < blockData.length; i++) {
					int data = blockData[i];

					int id = (data >> 4) & 0xFFF;
					int metadata = data & 0xF;

					int x = chunkX + ((data >> 28) & 0xF);
					int y = (data >> 16) & 0xFF;
					int z = chunkZ + ((data >> 24) & 0xF);

					eventBus.fire(new BlockChangeEvent(id, metadata, x, y, z));
				}
				break;
			}
			case 0x23: {
				PacketS23_BlockUpdate blockPacket = (PacketS23_BlockUpdate) packet;
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
				PacketS26_MultiChunkData chunkPacket = (PacketS26_MultiChunkData) packet;
				for(ChunkData chunk : chunkPacket.getChunks())
					processChunk(chunk, chunkPacket.hasSkylight(), true);
				break;
			}
			case 0x2D: {
				PacketS2D_OpenWindow windowOpenPacket = (PacketS2D_OpenWindow) packet;
				eventBus.fire(new WindowOpenEvent(	windowOpenPacket.getWindowId(),
													windowOpenPacket.getInventoryType(),
													windowOpenPacket.useWindowTitle() ? windowOpenPacket.getWindowTitle() : "",
													windowOpenPacket.getSlotCount()));
				break;
			}
			case 0x2E: {
				PacketS2E_CloseWindow windowClosePacket = (PacketS2E_CloseWindow) packet;
				eventBus.fire(new WindowCloseEvent(windowClosePacket.getWindowId()));
				break;
			}
			case 0x2F: {
				PacketS2F_SetSlot slotPacket = (PacketS2F_SetSlot) packet;
				eventBus.fire(new WindowSlotChangeEvent(slotPacket.getWindowId(), slotPacket.getSlot(), slotPacket.getItem()));
				break;
			}
			case 0x30: {
				PacketS30_WindowItems windowItemsPacket = (PacketS30_WindowItems) packet;
				eventBus.fire(new WindowUpdateEvent(windowItemsPacket.getWindowId(), windowItemsPacket.getItems()));
				break;
			}
			// TODO 0x31 WindowProperty
			case 0x33: {
				PacketS33_SignUpdate signUpdatePacket = (PacketS33_SignUpdate) packet;
				eventBus.fire(new SignUpdateEvent(signUpdatePacket.getX(), signUpdatePacket.getY(), signUpdatePacket.getZ(), signUpdatePacket.getLines()));
				break;
			}
			// TODO 0x34 MapUpdate
			case 0x35: {
				PacketS35_TileEntityUpdate tilePacket = (PacketS35_TileEntityUpdate) packet;
				eventBus.fire(new TileEntityUpdateEvent(tilePacket.getX(), tilePacket.getY(), tilePacket.getZ(), tilePacket.getAction(), tilePacket.getData()));
				break;
			}
			case 0x36: {
				PacketS36_OpenTileEditor tileEditorPacket = (PacketS36_OpenTileEditor) packet;
				eventBus.fire(new EditTileEntityEvent(tileEditorPacket.getX(), tileEditorPacket.getY(), tileEditorPacket.getZ()));
				break;
			}
			case 0x38: {
				PacketS38_PlayerListItem playerListEvent = (PacketS38_PlayerListItem) packet;
				if(playerListEvent.isOnline())
					eventBus.fire(new PlayerListUpdateEvent(playerListEvent.getPlayerName(), playerListEvent.getPing()));
				else
					eventBus.fire(new PlayerListRemoveEvent(playerListEvent.getPlayerName()));
				break;
			}
			// TODO 0x39 PlayerAbilities
			case 0x40: {
				PacketS40_Disconnect disconnectPacket = (PacketS40_Disconnect) packet;
				eventBus.fire(new KickEvent(disconnectPacket.getReason()));
			}
			}
			break;
		default:
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handleEncryption(PacketLS01_EncryptionRequest request) {
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

		connectionHandler.sendPacket(new PacketLC01_EncryptionResponse(secretKey, publicKey, request.getVerifyToken()));
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
