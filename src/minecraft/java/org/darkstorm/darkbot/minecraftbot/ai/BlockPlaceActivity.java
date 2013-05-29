package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.protocol.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public class BlockPlaceActivity implements Activity {
	private final MinecraftBot bot;

	private BlockLocation location;
	private int lastId, ticksWait;

	public BlockPlaceActivity(MinecraftBot bot, BlockLocation location) {
		this(bot, location, 5 * 20);
	}

	public BlockPlaceActivity(MinecraftBot bot, BlockLocation location, int timeout) {
		this.location = location;
		lastId = bot.getWorld().getBlockIdAt(location);
		this.bot = bot;
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		PlayerInventory inventory = player.getInventory();
		int originalX = location.getX(), originalY = location.getY(), originalZ = location.getZ();
		int face = getPlacementBlockFaceAt(location);
		System.out.println("Placing with face: " + face);
		if(face == -1)
			return;
		location = getOffsetBlock(location, face);
		if(location == null)
			return;
		int x = location.getX(), y = location.getY(), z = location.getZ();
		player.face(x + ((originalX - x) / 2.0D) + 0.5, y + ((originalY - y) / 2.0D), z + ((originalZ - z) / 2.0D) + 0.5);
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		connectionHandler.sendPacket(new Packet12PlayerLook((float) player.getYaw(), (float) player.getPitch(), true));
		connectionHandler.sendPacket(new Packet18Animation(player.getId(), Animation.SWING_ARM));
		Packet15Place placePacket = new Packet15Place();
		placePacket.xPosition = x;
		placePacket.yPosition = y;
		placePacket.zPosition = z;
		placePacket.direction = face;
		placePacket.itemStack = inventory.getCurrentHeldItem();
		connectionHandler.sendPacket(placePacket);
		ticksWait = timeout;
	}

	@Override
	public void run() {
		if(ticksWait > 0) {
			if(lastId != bot.getWorld().getBlockIdAt(location))
				ticksWait = 1;
			ticksWait--;
		}
	}

	@Override
	public boolean isActive() {
		return ticksWait > 0;
	}

	@Override
	public void stop() {
		ticksWait = 0;
	}

	private int getPlacementBlockFaceAt(BlockLocation location) {
		// Stack<Integer> blockFaces = new Stack<>();
		int x = location.getX(), y = location.getY(), z = location.getZ();
		World world = bot.getWorld();
		if(isPlaceable(world.getBlockIdAt(x, y - 1, z)))
			return 1;
		else if(isPlaceable(world.getBlockIdAt(x, y, z + 1)))
			return 2;
		else if(isPlaceable(world.getBlockIdAt(x, y, z - 1)))
			return 3;
		else if(isPlaceable(world.getBlockIdAt(x + 1, y, z)))
			return 4;
		else if(isPlaceable(world.getBlockIdAt(x - 1, y, z)))
			return 5;
		else if(isPlaceable(world.getBlockIdAt(x, y + 1, z)))
			return 0;
		return -1;
		/*if(z > z1) {
			
		}
		if(z == z1) {
			if(isPlaceable(world.getBlockIdAt(x, y, z - 1)))
				blockFaces.push(3);
			if(isPlaceable(world.getBlockIdAt(x, y, z + 1)))
				blockFaces.push(2);
		}
		if(x == x1) {
			if(isPlaceable(world.getBlockIdAt(x - 1, y, z)))
				blockFaces.push(5);
			if(isPlaceable(world.getBlockIdAt(x + 1, y, z)))
				blockFaces.push(4);
		}
		if(z > z1) {
			if(isPlaceable(world.getBlockIdAt(x, y, z - 1)))
				blockFaces.push(3);
			if(isPlaceable(world.getBlockIdAt(x, y, z + 1)))
				blockFaces.push(2);
		} else if(z < z1) {
			if(isPlaceable(world.getBlockIdAt(x, y, z + 1)))
				blockFaces.push(2);
			if(isPlaceable(world.getBlockIdAt(x, y, z - 1)))
				blockFaces.push(3);
		}
		if(y > y1) {
			if(isPlaceable(world.getBlockIdAt(x, y - 1, z)))
				blockFaces.push(1);
			if(isPlaceable(world.getBlockIdAt(x, y + 1, z)))
				blockFaces.push(0);
		} else {
			if(isPlaceable(world.getBlockIdAt(x, y + 1, z)))
				blockFaces.push(0);
			if(isPlaceable(world.getBlockIdAt(x, y - 1, z)))
				blockFaces.push(1);
		}
		if(x > x1) {
			if(isPlaceable(world.getBlockIdAt(x - 1, y, z)))
				blockFaces.push(5);
			if(isPlaceable(world.getBlockIdAt(x + 1, y, z)))
				blockFaces.push(4);
		}
		if(x < x1) {
			if(isPlaceable(world.getBlockIdAt(x + 1, y, z)))
				blockFaces.push(4);
			if(isPlaceable(world.getBlockIdAt(x - 1, y, z)))
				blockFaces.push(5);
		}
		if(blockFaces.isEmpty())
			return -1;
		return blockFaces.pop();*/
		/*if(isPlaceable(world.getBlockIdAt(x, y - 1, z))) {
			return 1;
		} else if(isPlaceable(world.getBlockIdAt(x, y, z + 1))) {
			return 2;
		} else if(isPlaceable(world.getBlockIdAt(x + 1, y, z))) {
			return 4;
		} else if(isPlaceable(world.getBlockIdAt(x, y, z - 1))) {
			return 3;
		} else if(isPlaceable(world.getBlockIdAt(x - 1, y, z))) {
			return 5;
		} else if(isPlaceable(world.getBlockIdAt(x, y + 1, z))) {
			return 0;
		} else
			return -1;*/
	}

	private boolean isPlaceable(int id) {
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
