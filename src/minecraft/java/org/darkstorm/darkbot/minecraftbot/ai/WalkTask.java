package org.darkstorm.darkbot.minecraftbot.ai;

import java.util.concurrent.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.*;

public class WalkTask implements Task {
	private final MinecraftBot bot;
	private final ExecutorService service = Executors.newSingleThreadExecutor();
	private BlockLocation target;
	private Future<PathNode> thread;
	private PathNode nextStep;
	private int ticksSinceStepChange = 0;
	private int timeout = 0;
	private long startTime = 0;
	private double speed = 0.12, jumpFactor = 2, fallFactor = 4,
			liquidFactor = 0.5;

	public WalkTask(MinecraftBot bot) {
		this.bot = bot;
	}

	public synchronized BlockLocation getTarget() {
		return target;
	}

	public synchronized void setTarget(final BlockLocation target) {
		if(thread != null && !thread.isDone()) {
			thread.cancel(true);
			thread = null;
		}
		boolean start = target != null;
		this.target = target;
		if(start)
			start();
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Walk speed, in blocks/tick. Default is 0.12.
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set walk speed.
	 * 
	 * @param speed
	 *            Walk speed, in blocks/tick. Default is 0.12.
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getJumpFactor() {
		return jumpFactor;
	}

	public void setJumpFactor(double jumpFactor) {
		this.jumpFactor = jumpFactor;
	}

	public double getFallFactor() {
		return fallFactor;
	}

	public void setFallFactor(double fallFactor) {
		this.fallFactor = fallFactor;
	}

	public double getLiquidFactor() {
		return liquidFactor;
	}

	public void setLiquidFactor(double liquidFactor) {
		this.liquidFactor = liquidFactor;
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return target != null;
	}

	@Override
	public synchronized boolean start(String... options) {
		if(options.length == 3) {
			try {
				int x = Integer.parseInt(options[0]);
				int y = Integer.parseInt(options[1]);
				int z = Integer.parseInt(options[2]);

				target = new BlockLocation(x, y, z);
			} catch(Exception e) {
				return false;
			}
		}
		if(target == null)
			return false;
		System.out.println("Searching for path to " + target);
		thread = service.submit(new Callable<PathNode>() {
			@Override
			public PathNode call() throws Exception {
				World world = bot.getWorld();
				MainPlayerEntity player = bot.getPlayer();
				if(world == null || player == null || target == null)
					return null;
				BlockLocation ourLocation = new BlockLocation((int) (Math
						.round(player.getX() - 0.5)), (int) player.getY(),
						(int) (Math.round(player.getZ() - 0.5)));
				PathSearch search = world.getPathFinder().provideSearch(
						ourLocation, target);
				while(!search.isDone() && !Thread.interrupted())
					search.step();
				System.out.println("Found path: " + search.getPath());
				return search.getPath();
			}
		});
		startTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public synchronized void stop() {
		target = null;
		if(thread != null && !thread.isDone()) {
			thread.cancel(true);
			thread = null;
		}
		nextStep = null;
		ticksSinceStepChange = 0;

		startTime = 0;
	}

	@Override
	public synchronized void run() {
		if(thread != null && !thread.isDone()) {
			if(timeout > 0 && System.currentTimeMillis() - startTime > timeout)
				stop();
			return;
		} else if(thread != null && thread.isDone()) {
			try {
				nextStep = thread.get();
			} catch(Exception exception) {
				exception.printStackTrace();
			}
			thread = null;
		}
		if(nextStep != null) {
			MainPlayerEntity player = bot.getPlayer();
			if(nextStep.getNext() != null
					&& player.getDistanceToSquared(nextStep.getLocation()) > player
							.getDistanceToSquared(nextStep.getNext()
									.getLocation()))
				nextStep = nextStep.getNext();
			if(player.getDistanceToSquared(nextStep.getLocation()) > 10) {
				stop();
				return;
			}
			ticksSinceStepChange++;
			if(ticksSinceStepChange > 80) {
				stop();
				return;
			}
			double speed = this.speed;
			WorldLocation location = nextStep.getLocation();
			double x = location.getX(), y = location.getY(), z = location
					.getZ();
			boolean inLiquid = player.isInLiquid();
			if(inLiquid)
				speed *= liquidFactor;
			if(player.getY() != y) {
				if(!inLiquid)
					if(player.getY() < y)
						speed *= jumpFactor;
					else
						speed *= fallFactor;
				player.setY(player.getY()
						+ (player.getY() < y ? Math.min(speed,
								y - player.getY()) : Math.max(-speed, y
								- player.getY())));
			}
			if(player.getX() != (x + 0.5D)) {
				player.setX(player.getX()
						+ (player.getX() < (x + 0.5D) ? Math.min(speed,
								(x + 0.5D) - player.getX()) : Math.max(-speed,
								(x + 0.5D) - player.getX())));
			}
			if(player.getZ() != (z + 0.5D)) {
				player.setZ(player.getZ()
						+ (player.getZ() < (z + 0.5D) ? Math.min(speed,
								(z + 0.5D) - player.getZ()) : Math.max(-speed,
								(z + 0.5D) - player.getZ())));
			}
			if(player.getX() == (x + 0.5D) && player.getY() == y
					&& player.getZ() == (z + 0.5D)) {
				nextStep = nextStep.getNext();
				ticksSinceStepChange = 0;
			}
		}
	}

	public synchronized boolean isMoving() {
		return nextStep != null;
	}

	@Override
	public synchronized boolean isActive() {
		return target != null && (thread != null || nextStep != null);
	}

	@Override
	public TaskPriority getPriority() {
		return TaskPriority.NORMAL;
	}

	@Override
	public boolean isExclusive() {
		return true;
	}

	@Override
	public boolean ignoresExclusive() {
		return false;
	}

	@Override
	public String getName() {
		return "Walk";
	}

	@Override
	public String getOptionDescription() {
		return "[x y z]";
	}
}
