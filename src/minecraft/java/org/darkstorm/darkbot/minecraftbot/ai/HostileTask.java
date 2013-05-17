package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet7UseEntity;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;

public class HostileTask implements Task {
	private final MinecraftBot bot;

	private boolean active = false;

	private int attackCooldown = 0;

	public HostileTask(MinecraftBot bot) {
		this.bot = bot;
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return false;
	}

	@Override
	public synchronized boolean start(String... options) {
		active = true;
		attackCooldown = 5;
		return true;
	}

	@Override
	public synchronized void stop() {
		active = false;
	}

	@Override
	public synchronized void run() {
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(world == null || player == null)
			return;
		if(attackCooldown > 0)
			attackCooldown--;
		Entity entity = null;
		int closestDistance = Integer.MAX_VALUE;
		for(Entity e : world.getEntities()) {
			if(!(e instanceof LivingEntity) || e.equals(bot.getPlayer()))
				continue;
			int distance = player.getDistanceToSquared(e);
			if(distance < closestDistance) {
				entity = e;
				closestDistance = distance;
			}
		}
		if(closestDistance > 500)
			return;
		player.face(entity.getX(), entity.getY() + 1, entity.getZ());
		if(closestDistance > 16) {
			BlockLocation location = new BlockLocation((int) entity.getX(),
					(int) (entity.getY() + 0.5), (int) entity.getZ());
			BlockLocation original = location;
			BlockLocation below = location.offset(0, -1, 0);
			while(!BlockType.getById(world.getBlockIdAt(below)).isSolid()
					&& !world.getPathFinder().getHeuristic()
							.isClimbableBlock(below)) {
				location = below;
				below = below.offset(0, -1, 0);
				if(original.getY() - location.getY() >= 5)
					return;
			}
			if(bot.hasActivity() && bot.getActivity() instanceof WalkActivity) {
				WalkActivity activity = (WalkActivity) bot.getActivity();
				if(location.getDistanceTo(activity.getTarget()) <= 3)
					return;
			}
			bot.setActivity(new WalkActivity(bot, location, true));
			return;
		} else {
			if(closestDistance < 9 && bot.hasActivity()
					&& bot.getActivity() instanceof WalkActivity)
				bot.setActivity(null);
			if(attackCooldown > 0)
				return;
			ConnectionHandler connectionHandler = bot.getConnectionHandler();
			connectionHandler.sendPacket(new Packet18Animation(player.getId(),
					Animation.SWING_ARM));
			connectionHandler.sendPacket(new Packet7UseEntity(player.getId(),
					entity.getId(), 1));
			attackCooldown = 5;
		}
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
		return true;
	}

	@Override
	public String getName() {
		return "AttackAll";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
