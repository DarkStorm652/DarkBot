package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;
import java.util.Arrays;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.EntityStatus;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.*;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TranslationMessage;
import com.github.steveice10.mc.protocol.packet.ingame.client.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.*;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventBus;
import org.darkstorm.minecraft.darkbot.event.EventHandler;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.EntitySpawnEvent.SpawnLocation;
import org.darkstorm.minecraft.darkbot.event.protocol.server.LivingEntitySpawnEvent.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.ObjectEntitySpawnEvent.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.PaintingSpawnEvent.PaintingSpawnLocation;
import org.darkstorm.minecraft.darkbot.event.protocol.server.RotatedEntitySpawnEvent.RotatedSpawnLocation;
import org.darkstorm.minecraft.darkbot.util.Bridge;
import org.darkstorm.minecraft.darkbot.world.PlayerInfo;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.BasicItemStack;


public final class Protocol335 implements EventListener {
	public static final int VERSION = 335;
	public static final String VERSION_NAME = "1.12";

	private final MinecraftBot bot;

	public Protocol335(MinecraftBot bot) {
		this.bot = bot;

		bot.getEventBus().register(this);
	}

	@EventHandler
	public void onInventoryChange(InventoryChangeEvent event) {
		Session handler = bot.getConnectionHandler();

		ClientWindowActionPacket packet = new ClientWindowActionPacket(event.getInventory().getWindowId(),
				event.getTransactionId(), event.getSlot(), Bridge.GetNewItemStack(event.getItem()), event.getWindowAction(), event.getWindowActionParam());

		handler.send(packet);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientCloseWindowPacket packet = new ClientCloseWindowPacket(event.getInventory().getWindowId());

		handler.send(packet);
	}

	@EventHandler
	public void onHeldItemDrop(HeldItemDropEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientPlayerActionPacket packet = new ClientPlayerActionPacket(event.isEntireStack() ?
				PlayerAction.DROP_ITEM_STACK : PlayerAction.DROP_ITEM, new Position(0,0,0), BlockFace.DOWN);

		handler.send(packet);
	}

	@EventHandler
	public void onHeldItemChange(HeldItemChangeEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientPlayerChangeHeldItemPacket packet = new ClientPlayerChangeHeldItemPacket(event.getNewSlot());

		handler.send(packet);
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		Session handler = bot.getConnectionHandler();

		InteractAction interactAction;
		if(event instanceof EntityHitEvent)
			interactAction = InteractAction.ATTACK;
		else if(event instanceof EntityUseEvent)
			interactAction = InteractAction.INTERACT;
		else
			return;

		ClientPlayerInteractEntityPacket packet = new ClientPlayerInteractEntityPacket(event.getEntity().getId(),
				interactAction, interactAction == InteractAction.ATTACK ? null : Hand.MAIN_HAND);

		handler.send(packet);
	}

	@EventHandler
	public void onArmSwing(ArmSwingEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientPlayerSwingArmPacket packet = new ClientPlayerSwingArmPacket(Hand.MAIN_HAND);

		handler.send(packet);
	}

	@EventHandler
	public void onCrouchUpdate(CrouchUpdateEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientPlayerStatePacket packet = new ClientPlayerStatePacket(bot.getPlayer().getId(), event.isCrouching() ?
				PlayerState.START_SNEAKING : PlayerState.START_SNEAKING);

		handler.send(packet);
	}

	@EventHandler
	public void onSprintUpdate(SprintUpdateEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientPlayerStatePacket packet = new ClientPlayerStatePacket(bot.getPlayer().getId(), event.isSprinting() ?
				PlayerState.START_SPRINTING : PlayerState.STOP_SPRINTING);

		handler.send(packet);
	}

	@EventHandler
	public void onBedLeave(BedLeaveEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientPlayerStatePacket packet = new ClientPlayerStatePacket(bot.getPlayer().getId(), PlayerState.LEAVE_BED);

		handler.send(packet);
	}

	@EventHandler
	public void onChatSent(ChatSentEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientChatPacket packet = new ClientChatPacket(event.getMessage());

		handler.send(packet);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		PlayerAction playerAction;
		if(event instanceof BlockBreakStartEvent)
			playerAction = PlayerAction.START_DIGGING;
		else if(event instanceof BlockBreakStopEvent)
			playerAction = PlayerAction.CANCEL_DIGGING;
		else if(event instanceof BlockBreakCompleteEvent)
			playerAction = PlayerAction.FINISH_DIGGING;
		else
			return;
		BlockFace face = BlockFace.values()[event.getFace()];

		Session handler = bot.getConnectionHandler();
		ClientPlayerActionPacket packet = new ClientPlayerActionPacket(playerAction, new Position(event.getX(), event.getY(), event.getZ()), face);

		handler.send(packet);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		BlockFace face = BlockFace.values()[event.getFace()];

		Session handler = bot.getConnectionHandler();
		ClientPlayerPlaceBlockPacket packet = new ClientPlayerPlaceBlockPacket(new Position(event.getX(), event.getY(),
				event.getZ()), face, Hand.MAIN_HAND, event.getXOffset(), event.getYOffset(), event.getZOffset());

		handler.send(packet);
	}

	@EventHandler
	public void onPlayerUpdate(PlayerUpdateEvent event) {
		double x = event.getX(), y = event.getY(), z = event.getZ();
		float yaw = event.getYaw(), pitch = event.getPitch();
		double eyeY = y + 1.62000000476837;
		boolean onGround = event.isOnGround();

		com.github.steveice10.packetlib.packet.Packet packet;
		if(event instanceof PlayerMoveRotateEvent)
			packet = new ClientPlayerPositionRotationPacket(onGround, x, y, z, yaw, pitch); //TODO: Z?
		else if (event instanceof PlayerMoveEvent)
			packet = new ClientPlayerPositionPacket(onGround, x, y, z); //TODO: Z?
		else if(event instanceof PlayerRotateEvent)
			packet = new ClientPlayerRotationPacket(onGround, yaw, pitch);
		else
			packet = new ClientPlayerMovementPacket(onGround);

		Session handler = bot.getConnectionHandler();
		handler.send(packet);
	}

	@EventHandler
	public void onItemUse(ItemUseEvent event) {
		Session handler = bot.getConnectionHandler();

		//TODO: Send ClientPlayerUseItemPacket too?
		ClientPlayerPlaceBlockPacket packet = new ClientPlayerPlaceBlockPacket(new Position(-1, -1, -1), BlockFace.SPECIAL, //TODO: Note that Y is unsigned so set to 255
				Hand.MAIN_HAND, 0,0,0);

		handler.send(packet);
	}

	@EventHandler
	public void onRequestRespawn(RequestRespawnEvent event) {
		Session handler = bot.getConnectionHandler();
		ClientRequestPacket packet = new ClientRequestPacket(ClientRequest.RESPAWN);

		handler.send(packet);
	}

	@EventHandler
	public void onRequestDisconnect(RequestDisconnectEvent event) {
		Session handler = bot.getConnectionHandler();
		ServerDisconnectPacket packet = new ServerDisconnectPacket(event.getReason());

		handler.send(packet);
	}

	public void onPacketReceived(com.github.steveice10.packetlib.event.session.PacketReceivedEvent event) {
		Packet packet = event.getPacket();
		Session handler = bot.getConnectionHandler();
		EventBus eventBus = bot.getEventBus();

		//System.out.println(packet);

		if(packet instanceof ServerJoinGamePacket)
		{
			ServerJoinGamePacket castPacket = (ServerJoinGamePacket) packet;
			eventBus.fire(new LoginEvent(castPacket.getEntityId(), castPacket.getWorldType(), castPacket.getGameMode(),
					castPacket.getDimension(), castPacket.getDifficulty(), 255, castPacket.getMaxPlayers())); // TODO: WorldHeight = const?
		}
		if(packet instanceof ServerChatPacket)
		{
			ServerChatPacket castPacket = (ServerChatPacket) packet;
			Message message = castPacket.getMessage();

			if(message instanceof TranslationMessage) {
				String formattedMessage = Arrays.toString(((TranslationMessage) message).getTranslationParams()).substring(1);
				formattedMessage = formattedMessage.substring(0, formattedMessage.length() - 1);
				eventBus.fire(new ChatReceivedEvent(formattedMessage));
			} else
				eventBus.fire(new ChatReceivedEvent(message.getFullText()));
		}
		else if(packet instanceof ServerUpdateTimePacket)
		{
			ServerUpdateTimePacket castPacket = (ServerUpdateTimePacket) packet;
			eventBus.fire(new TimeUpdateEvent(castPacket.getTime(), castPacket.getWorldAge()));
		}
		else if(packet instanceof ServerEntityEquipmentPacket)
		{
			ServerEntityEquipmentPacket castPacket = (ServerEntityEquipmentPacket) packet;
			eventBus.fire(new PlayerEquipmentUpdateEvent(castPacket.getEntityId(), castPacket.getSlot(), Bridge.GetOldItemStack(castPacket.getItem())));
		}
		else if(packet instanceof ServerPlayerHealthPacket)
		{
			ServerPlayerHealthPacket castPacket = (ServerPlayerHealthPacket) packet;
			eventBus.fire(new HealthUpdateEvent(castPacket.getHealth(), castPacket.getFood(), castPacket.getSaturation()));
		}
		else if(packet instanceof ServerRespawnPacket)
		{
			ServerRespawnPacket castPacket = (ServerRespawnPacket) packet;
			eventBus.fire(new RespawnEvent(castPacket.getDimension(),
					castPacket.getDifficulty(),
					castPacket.getGameMode(),
					castPacket.getWorldType(), 255)); //TODO: WorldHeight = const?
		}
		else if(packet instanceof ServerPlayerPositionRotationPacket)
		{
			ServerPlayerPositionRotationPacket castPacket = (ServerPlayerPositionRotationPacket) packet;
			handler.send(new ClientTeleportConfirmPacket(castPacket.getTeleportId()));
			eventBus.fire(new TeleportEvent(castPacket.getX(), castPacket.getY(), castPacket.getZ(), castPacket.getYaw(), castPacket.getPitch()));
		}
		else if(packet instanceof ServerPlayerUseBedPacket)
		{
			ServerPlayerUseBedPacket castPacket = (ServerPlayerUseBedPacket) packet;
			eventBus.fire(new SleepEvent(castPacket.getEntityId(), castPacket.getPosition().getX(), castPacket.getPosition().getY(), castPacket.getPosition().getZ()));
		}
		else if(packet instanceof ServerEntityAnimationPacket)
		{
			ServerEntityAnimationPacket castPacket = (ServerEntityAnimationPacket) packet;
			if(((ServerEntityAnimationPacket) packet).getAnimation() == Animation.EAT_FOOD)
				eventBus.fire(new EntityEatEvent(castPacket.getEntityId()));
		}
		else if(packet instanceof ServerSpawnPlayerPacket)
		{
			ServerSpawnPlayerPacket castPacket = (ServerSpawnPlayerPacket) packet;
			RotatedSpawnLocation location = new RotatedSpawnLocation(castPacket.getX(), castPacket.getY(),
					castPacket.getZ(), castPacket.getYaw(), castPacket.getPitch());
			BasicItemStack heldItem = new BasicItemStack(0, 1, 0); //TODO: ID = 0?
			eventBus.fire(new PlayerSpawnEvent(castPacket.getEntityId(), castPacket.getUUID().toString(), heldItem, location, castPacket.getMetadata()));
		}
		else if(packet instanceof ServerEntityCollectItemPacket)
		{
			ServerEntityCollectItemPacket castPacket = (ServerEntityCollectItemPacket) packet;
			eventBus.fire(new EntityCollectEvent(castPacket.getCollectedEntityId(), castPacket.getCollectorEntityId()));
		}
		else if(packet instanceof ServerSpawnObjectPacket)
		{
			ServerSpawnObjectPacket castPacket = (ServerSpawnObjectPacket) packet;
			RotatedSpawnLocation location = new RotatedSpawnLocation(castPacket.getX(), castPacket.getY(),
					castPacket.getZ(), castPacket.getYaw(), castPacket.getPitch());
			ObjectSpawnData spawnData;
			if(castPacket.getEntityId() != 0)
				spawnData = new ThrownObjectSpawnData(castPacket.getType(), castPacket.getEntityId(),
						castPacket.getMotionX(), castPacket.getMotionY(), castPacket.getMotionZ());
			else
				spawnData = new ObjectSpawnData(castPacket.getType());
			eventBus.fire(new ObjectEntitySpawnEvent(castPacket.getEntityId(), location, spawnData));
		}
		else if(packet instanceof ServerSpawnMobPacket)
		{
			ServerSpawnMobPacket castPacket = (ServerSpawnMobPacket) packet;
			LivingEntitySpawnLocation location = new LivingEntitySpawnLocation(castPacket.getX(), castPacket.getY(),
					castPacket.getZ(), castPacket.getYaw(), castPacket.getPitch(), castPacket.getHeadYaw());
			LivingEntitySpawnData data = new LivingEntitySpawnData(castPacket.getType(), castPacket.getMotionX(), castPacket.getMotionY(), castPacket.getMotionZ());
			eventBus.fire(new LivingEntitySpawnEvent(castPacket.getEntityId(), location, data, castPacket.getMetadata()));
		}
		else if(packet instanceof ServerSpawnPaintingPacket)
		{
			ServerSpawnPaintingPacket castPacket = (ServerSpawnPaintingPacket) packet;
			PaintingSpawnLocation location = new PaintingSpawnLocation(castPacket.getPosition().getX(),
					castPacket.getPosition().getY(), castPacket.getPosition().getZ(), castPacket.getDirection());
			eventBus.fire(new PaintingSpawnEvent(castPacket.getEntityId(), location, castPacket.getPaintingType().name()));
		}
		else if(packet instanceof ServerSpawnExpOrbPacket)
		{
			ServerSpawnExpOrbPacket castPacket = (ServerSpawnExpOrbPacket) packet;
			SpawnLocation location = new SpawnLocation(castPacket.getX(), castPacket.getY(), castPacket.getZ());
			eventBus.fire(new ExpOrbSpawnEvent(castPacket.getEntityId(), location, castPacket.getExp()));
		}
		else if(packet instanceof ServerEntityVelocityPacket)
		{
			ServerEntityVelocityPacket castPacket = (ServerEntityVelocityPacket) packet;
			eventBus.fire(new EntityVelocityEvent(castPacket.getEntityId(), castPacket.getMotionX(),
					castPacket.getMotionY(), castPacket.getMotionZ()));
		}
		else if(packet instanceof ServerEntityDestroyPacket)
		{
			ServerEntityDestroyPacket castPacket = (ServerEntityDestroyPacket) packet;
			for(int id : castPacket.getEntityIds())
				eventBus.fire(new EntityDespawnEvent(id));
		}
		else if(packet instanceof ServerEntityPositionPacket)
		{
			ServerEntityPositionPacket castPacket = (ServerEntityPositionPacket) packet;
			eventBus.fire(new EntityMoveEvent(castPacket.getEntityId(), castPacket.getMovementX(), castPacket.getMovementY(), castPacket.getMovementZ()));
		}
		else if(packet instanceof ServerEntityPositionRotationPacket)
		{
			ServerEntityPositionRotationPacket castPacket = (ServerEntityPositionRotationPacket) packet;
			eventBus.fire(new EntityMoveEvent(castPacket.getEntityId(), castPacket.getMovementX(), castPacket.getMovementY(), castPacket.getMovementZ()));
		}
		else if(packet instanceof ServerEntityRotationPacket)
		{
			ServerEntityRotationPacket castPacket = (ServerEntityRotationPacket) packet;
			eventBus.fire(new EntityRotateEvent(castPacket.getEntityId(), castPacket.getYaw(), castPacket.getPitch()));
		}
		else if(packet instanceof ServerEntityTeleportPacket)
		{
			ServerEntityTeleportPacket castPacket = (ServerEntityTeleportPacket) packet;
			eventBus.fire(new EntityTeleportEvent(castPacket.getEntityId(), castPacket.getX(), castPacket.getY(),
					castPacket.getZ(), castPacket.getYaw(), castPacket.getPitch()));
		}
		else if(packet instanceof ServerEntityHeadLookPacket)
		{
			ServerEntityHeadLookPacket castPacket = (ServerEntityHeadLookPacket) packet;
			eventBus.fire(new EntityHeadRotateEvent(castPacket.getEntityId(), castPacket.getHeadYaw()));
		}
		else if(packet instanceof ServerEntityStatusPacket)
		{
			ServerEntityStatusPacket castPacket = (ServerEntityStatusPacket) packet;
			if(castPacket.getStatus() == EntityStatus.LIVING_HURT || castPacket.getStatus() == EntityStatus.LIVING_HURT_THORNS)
				eventBus.fire(new EntityHurtEvent(castPacket.getEntityId()));
			else if(castPacket.getStatus() == EntityStatus.LIVING_DEATH)
				eventBus.fire(new EntityDeathEvent(castPacket.getEntityId()));
			else if(castPacket.getStatus() == EntityStatus.PLAYER_FINISH_USING_ITEM)
				eventBus.fire(new EntityStopEatingEvent(castPacket.getEntityId()));
		}
		else if(packet instanceof ServerEntityAttachPacket)
		{
			//TODO: Check if entity IDs are not reversed
			ServerEntityAttachPacket castPacket = (ServerEntityAttachPacket) packet;
			if(castPacket.getEntityId() != -1)
				eventBus.fire(new EntityMountEvent(castPacket.getEntityId(), castPacket.getAttachedToId()));
			else
				eventBus.fire(new EntityDismountEvent(castPacket.getEntityId()));
		}
		else if(packet instanceof ServerEntityMetadataPacket)
		{
			ServerEntityMetadataPacket castPacket = (ServerEntityMetadataPacket) packet;
			eventBus.fire(new EntityMetadataUpdateEvent(castPacket.getEntityId(), castPacket.getMetadata()));
		}
		else if(packet instanceof ServerPlayerSetExperiencePacket)
		{
			ServerPlayerSetExperiencePacket castPacket = (ServerPlayerSetExperiencePacket) packet;
			eventBus.fire(new ExperienceUpdateEvent(castPacket.getLevel(), castPacket.getTotalExperience()));
		}
		else if(packet instanceof ServerMapDataPacket)
		{
			//TODO: Implement
			//ServerMapDataPacket castPacket = (ServerMapDataPacket) packet;
			//processChunk(mapChunkPacket.x, mapChunkPacket.z, mapChunkPacket.chunkData, mapChunkPacket.bitmask, mapChunkPacket.additionalBitmask, true, mapChunkPacket.biomes);
		}
		else if(packet instanceof ServerMultiBlockChangePacket)
		{
			ServerMultiBlockChangePacket castPacket = (ServerMultiBlockChangePacket) packet;
			if(castPacket.getRecords() == null)
				return;

			for(BlockChangeRecord record : castPacket.getRecords())
				eventBus.fire(new BlockChangeEvent(record.getBlock().getId(), record.getBlock().getData(),
						record.getPosition().getX(), record.getPosition().getY(), record.getPosition().getZ()));

		}
		else if(packet instanceof ServerBlockChangePacket)
		{
			ServerBlockChangePacket castPacket = (ServerBlockChangePacket) packet;

			BlockChangeRecord blockChangeRecord = castPacket.getRecord();
			BlockState blockState = castPacket.getRecord().getBlock();

			eventBus.fire(new BlockChangeEvent(blockState.getId(), blockState.getData(), blockChangeRecord.getPosition().getX(),
					blockChangeRecord.getPosition().getY(), blockChangeRecord.getPosition().getZ()));
		}
		else if(packet instanceof ServerChunkDataPacket)
		{
			ServerChunkDataPacket castPacket = (ServerChunkDataPacket) packet;
			Column column = castPacket.getColumn();
			Chunk[] chunks = castPacket.getColumn().getChunks();
			for (int y = 0; y < chunks.length; y++) {
				Chunk chunk = chunks[y];
				if(chunk == null)
					continue;
				//TODO: NBT metadata and biome data
				ChunkLoadEvent newEvent = new ChunkLoadEvent(column.getX(), y, column.getZ(), chunk.getBlocks(), chunk.getBlockLight(), chunk.getSkyLight(), null);
				eventBus.fire(newEvent);
			}
		}
		else if(packet instanceof ServerOpenWindowPacket)
		{
			ServerOpenWindowPacket castPacket = (ServerOpenWindowPacket) packet;
			eventBus.fire(new WindowOpenEvent(castPacket.getWindowId(), castPacket.getType(), castPacket.getName(), castPacket.getSlots()));
		}
		else if(packet instanceof ServerCloseWindowPacket)
		{
			ServerCloseWindowPacket castPacket = (ServerCloseWindowPacket) packet;
			eventBus.fire(new WindowCloseEvent(castPacket.getWindowId()));
		}
		else if(packet instanceof ServerSetSlotPacket)
		{
			ServerSetSlotPacket castPacket = (ServerSetSlotPacket) packet;

			eventBus.fire(new WindowSlotChangeEvent(castPacket.getWindowId(), castPacket.getSlot(), Bridge.GetOldItemStack(castPacket.getItem())));
		}
		else if(packet instanceof ServerWindowItemsPacket)
		{
			ServerWindowItemsPacket castPacket = (ServerWindowItemsPacket) packet;
			ItemStack[] newItemStacks = castPacket.getItems();
			BasicItemStack[] basicItemStacks = new BasicItemStack[newItemStacks.length];

			for (int i = 0; i < newItemStacks.length; i++) {
				basicItemStacks[i] = Bridge.GetOldItemStack(newItemStacks[i]);
			}

			eventBus.fire(new WindowUpdateEvent(castPacket.getWindowId(), basicItemStacks));
		}
		else if(packet instanceof ServerUpdateTileEntityPacket)
		{
			ServerUpdateTileEntityPacket castPacket = (ServerUpdateTileEntityPacket) packet;
			eventBus.fire(new TileEntityUpdateEvent(castPacket.getPosition().getX(), castPacket.getPosition().getY(),
					castPacket.getPosition().getZ(), castPacket.getType(), castPacket.getNBT()));
		}
		else if(packet instanceof ServerOpenTileEntityEditorPacket)
		{
			ServerOpenTileEntityEditorPacket castPacket = (ServerOpenTileEntityEditorPacket) packet;
			eventBus.fire(new EditTileEntityEvent(castPacket.getPosition().getX(), castPacket.getPosition().getY(), castPacket.getPosition().getZ()));
		}
		else if(packet instanceof ServerPlayerListEntryPacket)
		{
			ServerPlayerListEntryPacket castPacket = (ServerPlayerListEntryPacket) packet;
			if(castPacket.getAction() == PlayerListEntryAction.ADD_PLAYER || castPacket.getAction() == PlayerListEntryAction.UPDATE_DISPLAY_NAME
					|| castPacket.getAction() == PlayerListEntryAction.UPDATE_LATENCY)
			{
				for(PlayerListEntry entry : castPacket.getEntries())
					eventBus.fire(new PlayerListUpdateEvent(new PlayerInfo(entry.getProfile().getIdAsString(), entry.getProfile().getName())));

			} else if(castPacket.getAction() == PlayerListEntryAction.REMOVE_PLAYER) {
				for(PlayerListEntry entry : castPacket.getEntries())
					eventBus.fire(new PlayerListRemoveEvent(new PlayerInfo(entry.getProfile().getIdAsString(), entry.getProfile().getName())));
			}
		}
		else if(packet instanceof ServerDisconnectPacket)
		{
			ServerDisconnectPacket castPacket = (ServerDisconnectPacket) packet;
			eventBus.fire(new KickEvent(castPacket.getReason().getText()));
		}
	}
}
