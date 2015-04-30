package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.event.EventBus;
import org.darkstorm.darkbot.minecraftbot.event.protocol.client.*;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.ToolType;

public class BlockBreakActivity implements Activity {
	private final MinecraftBot bot;

	private BlockLocation location;
	private int lastId, timeout, wait;
	private boolean breaking;

	public BlockBreakActivity(MinecraftBot bot, BlockLocation location) {
		this(bot, location, 10 * 20);
	}

	public BlockBreakActivity(MinecraftBot bot, BlockLocation location, int timeout) {
		this.location = location;
		this.lastId = bot.getWorld().getBlockIdAt(location);
		this.bot = bot;
		this.timeout = timeout;
		
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return;
		int face = getBreakBlockFaceAt(location);
		if(face == -1)
			return;
		
		int x = location.getX(), y = location.getY(), z = location.getZ();
		player.face(x + 0.5, y + 0.5, z + 0.5);
		
		int idAbove = world.getBlockIdAt(x, y + 1, z);
		if(idAbove == 12 || idAbove == 13)
			wait = 10;
		
		ToolType toolType = BlockType.getById(world.getBlockIdAt(location)).getToolType();
		if(toolType != null)
			player.switchTools(toolType);
		
	}

	@Override
	public void run() {
		MainPlayerEntity player = bot.getPlayer();
		int x = location.getX(), y = location.getY(), z = location.getZ();
		player.face(x + 0.5, y + 0.5, z + 0.5);
		
		if(!breaking) {
			int face = getBreakBlockFaceAt(location);
			if(face == -1) {
				timeout = wait = 0;
				return;
			}
			EventBus eventBus = bot.getEventBus();
			eventBus.fire(new ArmSwingEvent());
			eventBus.fire(new BlockBreakStartEvent(x, y, z, face));
			eventBus.fire(new BlockBreakCompleteEvent(x, y, z, face));
			breaking = true;
		}
		
		if(timeout > 0) {
			if(lastId != bot.getWorld().getBlockIdAt(location))
				timeout = 0;
			else if(--timeout == 0)
				wait = 0;
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
		return !type.isSolid();
	}
}
