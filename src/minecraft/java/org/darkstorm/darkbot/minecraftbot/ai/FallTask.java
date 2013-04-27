package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockType;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class FallTask implements Task {
	private final MinecraftBot bot;

	public FallTask(MinecraftBot bot) {
		this.bot = bot;
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		return !player.isOnGround();
	}

	@Override
	public synchronized boolean start(String... options) {
		return isPreconditionMet();
	}

	@Override
	public synchronized void stop() {
	}

	@Override
	public synchronized void run() {
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return;
		double speed = 0.18 * 3;
		double x = player.getX(), z = player.getZ();
		int lowestY = (int) player.getY();
		while(!BlockType.getById(
				world.getBlockIdAt((int) (x - 0.5), (lowestY - 1),
						(int) (z - 0.5))).isSolid()
				&& lowestY > 0)
			lowestY--;
		player.setY(player.getY() + Math.max(-speed, lowestY - player.getY()));
	}

	@Override
	public synchronized boolean isActive() {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		return !player.isOnGround();
	}

	@Override
	public TaskPriority getPriority() {
		return TaskPriority.NORMAL;
	}

	@Override
	public boolean isExclusive() {
		return false;
	}

	@Override
	public boolean ignoresExclusive() {
		return false;
	}

	@Override
	public String getName() {
		return "Fall";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
