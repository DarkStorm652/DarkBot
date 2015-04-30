package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventBus;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.*;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.PlayerInventory;

public class BlockPlaceActivity implements Activity {
	public static final int DEFAULT_TIMEOUT = 40;

	private final MinecraftBot bot;

	private BlockLocation location, target;
	private int lastId, timeout, face;
	private boolean placing;

	public BlockPlaceActivity(MinecraftBot bot, BlockLocation location) {
		this(bot, location, DEFAULT_TIMEOUT);
	}

	public BlockPlaceActivity(MinecraftBot bot, BlockLocation location, int timeout) {
		this(bot, location, timeout, getPlacementBlockFaceAt(bot.getWorld(), location));
	}

	public BlockPlaceActivity(MinecraftBot bot, BlockLocation location, byte face) {
		this(bot, location, DEFAULT_TIMEOUT, face);
	}

	public BlockPlaceActivity(MinecraftBot bot, BlockLocation location, int timeout, byte face) {
		this.location = location;
		this.lastId = bot.getWorld().getBlockIdAt(location);
		this.bot = bot;
		this.timeout = timeout;
		this.face = face;
		
		MainPlayerEntity player = bot.getPlayer();
		if(player == null) {
			timeout = 0;
			return;
		}
		
		int originalX = location.getX(), originalY = location.getY(), originalZ = location.getZ();
		System.out.println("Placing with face: " + face);
		if(face == -1) {
			timeout = 0;
			return;
		}
		
		this.target = getOffsetBlock(location, face);
		if(target == null) {
			timeout = 0;
			return;
		}
		int x = target.getX(), y = target.getY(), z = target.getZ();
		player.face(x + ((originalX - x) / 2.0D) + 0.5, y + ((originalY - y) / 2.0D), z + ((originalZ - z) / 2.0D) + 0.5);
	}

	@Override
	public void run() {
		MainPlayerEntity player = bot.getPlayer();
		int originalX = location.getX(), originalY = location.getY(), originalZ = location.getZ();
		int x = target.getX(), y = target.getY(), z = target.getZ();
		player.face(x + ((originalX - x) / 2.0D) + 0.5, y + ((originalY - y) / 2.0D), z + ((originalZ - z) / 2.0D) + 0.5);
		
		if(!placing) {
			PlayerInventory inventory = player.getInventory();
			EventBus eventBus = bot.getEventBus();
			eventBus.fire(new ArmSwingEvent());
			eventBus.fire(new BlockPlaceEvent(inventory.getCurrentHeldItem(), x, y, z, face));
			placing = true;
		}
		
		if(timeout > 0) {
			if(lastId != bot.getWorld().getBlockIdAt(location))
				timeout = 0;
			else
				timeout--;
		}
	}

	@Override
	public boolean isActive() {
		return timeout > 0 && target != null;
	}

	@Override
	public void stop() {
		timeout = 0;
	}

	private static byte getPlacementBlockFaceAt(World world, BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		if(isPlaceable(world.getBlockIdAt(x, y - 1, z)))
			return 1;
		else if(isPlaceable(world.getBlockIdAt(x + 1, y, z)))
			return 4;
		else if(isPlaceable(world.getBlockIdAt(x - 1, y, z)))
			return 5;
		else if(isPlaceable(world.getBlockIdAt(x, y, z + 1)))
			return 2;
		else if(isPlaceable(world.getBlockIdAt(x, y, z - 1)))
			return 3;
		else if(isPlaceable(world.getBlockIdAt(x, y + 1, z)))
			return 0;
		return -1;
	}

	private static boolean isPlaceable(int id) {
		BlockType type = BlockType.getById(id);
		return type.isPlaceable() && !type.isInteractable();
	}

	private BlockLocation getOffsetBlock(BlockLocation location, int face) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		switch(face) {
		case 0:
			y++;
			break;
		case 1:
			y--;
			break;
		case 2:
			z++;
			break;
		case 3:
			z--;
			break;
		case 4:
			x++;
			break;
		case 5:
			x--;
			break;
		default:
			return null;
		}
		return new BlockLocation(x, y, z);
	}
}
