package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.*;
import org.darkstorm.minecraft.darkbot.world.entity.*;

public class HostileTask extends AbstractTask {
	private boolean active = false;

	private int attackCooldown = 0;

	public HostileTask(MinecraftBot bot) {
		super(bot);
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
			BlockLocation location = new BlockLocation(entity.getLocation());
			BlockLocation original = location;
			BlockLocation below = location.offset(0, -1, 0);
			while(!BlockType.getById(world.getBlockIdAt(below)).isSolid() && !world.getPathFinder().getWorldPhysics().canClimb(below)) {
				location = below;
				below = below.offset(0, -1, 0);
				if(original.getY() - location.getY() >= 5)
					return;
			}
			if(hasActivity() && getActivity() instanceof WalkActivity) {
				WalkActivity activity = (WalkActivity) getActivity();
				if(location.getDistanceTo(activity.getTarget()) <= 3)
					return;
			}
			setActivity(new WalkActivity(bot, location, true));
			return;
		} else {
			if(closestDistance < 9 && hasActivity() && getActivity() instanceof WalkActivity)
				setActivity(null);
			if(attackCooldown > 0)
				return;
			player.hit(entity);
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
