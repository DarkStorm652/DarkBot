package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class BlockBreakActivity implements Activity {
	private final MinecraftBot bot;

	private BlockLocation location;
	private int lastId, timeout, wait;

	public BlockBreakActivity(MinecraftBot bot, BlockLocation location) {
		this(bot, location, 10 * 20);
	}

	public BlockBreakActivity(MinecraftBot bot, BlockLocation location,
			int timeout) {
		this.location = location;
		lastId = bot.getWorld().getBlockIdAt(location);
		this.bot = bot;
		int x = location.getX(), y = location.getY(), z = location.getZ();
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null)
			return;
		int face = getBreakBlockFaceAt(location);
		if(face == -1)
			return;
		player.face(x, y, z);
		int idAbove = world.getBlockIdAt(x, y + 1, z);
		if(idAbove == 12 || idAbove == 13)
			wait = 30;
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		player.switchTools(BlockType.getById(world.getBlockIdAt(location))
				.getToolType());
		connectionHandler.sendPacket(new Packet12PlayerLook((float) player
				.getYaw(), (float) player.getPitch(), true));
		connectionHandler.sendPacket(new Packet18Animation(player.getId(),
				Animation.SWING_ARM));
		connectionHandler.sendPacket(new Packet14BlockDig(0, x, y, z, face));
		connectionHandler.sendPacket(new Packet14BlockDig(2, x, y, z, face));
		this.timeout = timeout;
	}

	@Override
	public void run() {
		if(timeout > 0) {
			if(lastId != bot.getWorld().getBlockIdAt(location))
				timeout = 1;
			timeout--;
		} else if(wait > 0)
			wait--;
	}

	@Override
	public boolean isActive() {
		return timeout > 0 || wait > 0;
	}

	@Override
	public void stop() {
		wait = 0;
		timeout = 0;
	}

	private int getBreakBlockFaceAt(BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		World world = bot.getWorld();
		if(isEmpty(world.getBlockIdAt(x - 1, y, z)))
			return 4;
		else if(isEmpty(world.getBlockIdAt(x, y, z + 1)))
			return 3;
		else if(isEmpty(world.getBlockIdAt(x, y, z - 1)))
			return 2;
		else if(isEmpty(world.getBlockIdAt(x, y + 1, z)))
			return 1;
		else if(isEmpty(world.getBlockIdAt(x, y - 1, z)))
			return 0;
		else if(isEmpty(world.getBlockIdAt(x + 1, y, z)))
			return 5;
		return -1;
	}

	private boolean isEmpty(int id) {
		BlockType type = BlockType.getById(id);
		return !type.isSolid() && !type.isInteractable() && !type.isPlaceable();
	}
}
