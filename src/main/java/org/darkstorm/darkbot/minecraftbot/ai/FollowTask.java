package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.util.Util;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;

public class FollowTask implements Task {
	private final MinecraftBot bot;
	private Entity following = null;
	private BlockLocation lastLocation;

	public FollowTask(MinecraftBot bot) {
		this.bot = bot;
	}

	public synchronized void follow(Entity entity) {
		following = entity;
	}

	public synchronized Entity following() {
		return following;
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return following != null;
	}

	@Override
	public synchronized boolean start(String... options) {
		if(options.length > 0) {
			String name = options[0];
			World world = bot.getWorld();
			if(world == null)
				return false;
			for(Entity entity : world.getEntities())
				if(entity instanceof PlayerEntity && name.equalsIgnoreCase(Util.stripColors(((PlayerEntity) entity).getName())))
					following = entity;
		}
		return following != null;
	}

	@Override
	public void stop() {
		following = null;
	}

	@Override
	public void run() {
		MainPlayerEntity player = bot.getPlayer();
		if(following == null || player == null)
			return;
		BlockLocation location = new BlockLocation(following.getLocation());
		if((int) Math.ceil(following.getY()) != location.getY() && following.isOnGround())
			location = location.offset(0, 1, 0);
		if(lastLocation == null || !lastLocation.equals(location)) {
			lastLocation = location;
			System.out.println("Checking location " + location);
			World world = bot.getWorld();
			System.out.println(world.getChunkAt(new ChunkLocation(location)));
			BlockLocation original = location;
			BlockLocation below = location.offset(0, -1, 0);
			while(!BlockType.getById(world.getBlockIdAt(below)).isSolid() && !world.getPathFinder().getWorldPhysics().canClimb(below)) {
				location = below;
				below = below.offset(0, -1, 0);
				if(original.getY() - location.getY() >= 5)
					return;
			}
			bot.setActivity(new WalkActivity(bot, location, true));
		}
	}

	@Override
	public boolean isActive() {
		boolean active = following != null;
		if(active) {
			MainPlayerEntity player = bot.getPlayer();
			if(player == null)
				return true;
			player.face(following.getX(), following.getY() + 1, following.getZ());
			Activity activity = bot.getActivity();
			if(activity == null || !(activity instanceof WalkActivity))
				return active;
			WalkActivity walkActivity = (WalkActivity) activity;
			if(walkActivity.isActive() && player.isOnGround() && (player.getDistanceTo(following) < 2 || following.getDistanceTo(walkActivity.getTarget()) > 3))
				bot.setActivity(null);
		}
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
		return "Follow";
	}

	@Override
	public String getOptionDescription() {
		return "[player]";
	}
}
