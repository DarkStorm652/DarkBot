package org.darkstorm.minecraft.darkbot.ai;

import java.util.Random;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public class DerpTask extends AbstractTask {
	private final Random random = new Random();

	private boolean active;

	public DerpTask(MinecraftBot bot) {
		super(bot);
	}

	@Override
	public boolean isPreconditionMet() {
		return active;
	}

	@Override
	public boolean start(String... options) {
		active = true;
		return true;
	}

	@Override
	public void stop() {
		active = false;
	}

	@Override
	public void run() {
		MainPlayerEntity player = bot.getPlayer();
		player.setYaw(random.nextDouble() * 360);
		player.setPitch(random.nextDouble() * 360);
	}

	@Override
	public synchronized boolean isActive() {
		return active;
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
		return "Derp";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
