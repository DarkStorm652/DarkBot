package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.protocol.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class AvoidDeathTask implements Task {
	private final MinecraftBot bot;

	private boolean enabled;
	private int lastHealth;

	public AvoidDeathTask(MinecraftBot bot) {
		this.bot = bot;
	}

	@Override
	public boolean isPreconditionMet() {
		return enabled;
	}

	@Override
	public boolean start(String... options) {
		enabled = true;
		return true;
	}

	@Override
	public void stop() {
		enabled = false;
		lastHealth = 0;
	}

	@Override
	public void run() {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null) {
			lastHealth = 0;
			return;
		}
		if(lastHealth == 0) {
			lastHealth = player.getHealth();
			return;
		}
		if(player.getHealth() < lastHealth) {
			ConnectionHandler connectionHandler = bot.getConnectionHandler();
			connectionHandler.disconnect("Damaged! Disconnecting! Position: "
					+ player.getLocation());
			enabled = false;
		}
	}

	@Override
	public boolean isActive() {
		return enabled;
	}

	@Override
	public TaskPriority getPriority() {
		return TaskPriority.HIGHEST;
	}

	@Override
	public boolean isExclusive() {
		return false;
	}

	@Override
	public boolean ignoresExclusive() {
		return true;
	}

	@Override
	public String getName() {
		return "Avoid Death";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
