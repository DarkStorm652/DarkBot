package org.darkstorm.darkbot.minecraftbot.ai;

import java.util.concurrent.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.*;

public class WalkActivity implements Activity {
	private static double defaultSpeed = 0.15, defaultJumpFactor = 3, defaultFallFactor = 4, defaultLiquidFactor = 0.5;
	private static int defaultTimeout = 60000;

	private final MinecraftBot bot;
	private final ExecutorService service = Executors.newSingleThreadExecutor();
	private final BlockLocation target;

	private final long startTime;

	private Future<PathNode> thread;
	private PathNode nextStep;
	private int ticksSinceStepChange = 0;
	private int timeout = defaultTimeout;
	private double speed = defaultSpeed, jumpFactor = defaultJumpFactor, fallFactor = defaultFallFactor, liquidFactor = defaultLiquidFactor;

	public WalkActivity(MinecraftBot bot, BlockLocation target) {
		this(bot, target, false);
	}

	public WalkActivity(final MinecraftBot bot, final BlockLocation target, boolean keepWalking) {
		this.bot = bot;
		this.target = target;
		System.out.println("Walking!");
		if(keepWalking) {
			Activity activity = bot.getActivity();
			if(activity != null && activity instanceof WalkActivity && ((WalkActivity) activity).isMoving()) {
				WalkActivity walkActivity = (WalkActivity) activity;
				nextStep = walkActivity.nextStep;
				ticksSinceStepChange = walkActivity.ticksSinceStepChange;
			}
		}
		thread = service.submit(new Callable<PathNode>() {
			@Override
			public PathNode call() throws Exception {
				World world = bot.getWorld();
				MainPlayerEntity player = bot.getPlayer();
				if(world == null || player == null || target == null)
					return null;
				BlockLocation ourLocation = new BlockLocation(player.getLocation());
				PathSearch search = world.getPathFinder().provideSearch(ourLocation, target);
				while(!search.isDone() && (thread == null || !thread.isCancelled())) {
					System.out.println("Stepping...");
					search.step();
				}
				return search.getPath();
			}
		});
		startTime = System.currentTimeMillis();
	}

	public BlockLocation getTarget() {
		return target;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Walk speed, in blocks/tick. Default is 0.15.
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set walk speed.
	 * 
	 * @param speed
	 *            Walk speed, in blocks/tick. Default is 0.15.
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
	public void run() {
		if(thread != null && !thread.isDone()) {
			if(timeout > 0 && System.currentTimeMillis() - startTime > timeout) {
				thread.cancel(true);
				thread = null;
				nextStep = null;
				return;
			}
		} else if(thread != null && thread.isDone() && !thread.isCancelled()) {
			try {
				nextStep = thread.get();
				System.out.println("Path found, walking...");
				ticksSinceStepChange = 0;
			} catch(Exception exception) {
				exception.printStackTrace();
				nextStep = null;
				return;
			} finally {
				thread = null;
			}
		}
		if(nextStep != null) {
			MainPlayerEntity player = bot.getPlayer();
			System.out.println(" -> Moving from " + player.getLocation() + " to " + nextStep);
			if(nextStep.getNext() != null && player.getDistanceToSquared(nextStep.getNext().getLocation()) < 0.2) {
				nextStep = nextStep.getNext();
				ticksSinceStepChange = 0;
			}
			if(player.getDistanceToSquared(nextStep.getLocation()) > 4) {
				nextStep = null;
				return;
			}
			ticksSinceStepChange++;
			if(ticksSinceStepChange > 80) {
				nextStep = null;
				return;
			}
			double speed = this.speed;
			BlockLocation location = nextStep.getLocation();
			BlockLocation block = new BlockLocation(player.getLocation());
			double x = location.getX() + 0.5, y = location.getY(), z = location.getZ() + 0.5;
			boolean inLiquid = player.isInLiquid();
			if(BlockType.getById(bot.getWorld().getBlockIdAt(block.offset(0, -1, 0))) == BlockType.SOUL_SAND) {
				if(BlockType.getById(bot.getWorld().getBlockIdAt(location.offset(0, -1, 0))) == BlockType.SOUL_SAND)
					y -= 0.12;
				speed *= liquidFactor;
			} else if(inLiquid)
				speed *= liquidFactor;
			if(player.getY() != y) {
				if(!inLiquid && !bot.getWorld().getPathFinder().getWorldPhysics().canClimb(block))
					if(player.getY() < y)
						speed *= jumpFactor;
					else
						speed *= fallFactor;
				player.setY(player.getY() + (player.getY() < y ? Math.min(speed, y - player.getY()) : Math.max(-speed, y - player.getY())));
			}
			if(player.getX() != x)
				player.setX(player.getX() + (player.getX() < x ? Math.min(speed, x - player.getX()) : Math.max(-speed, x - player.getX())));
			if(player.getZ() != z)
				player.setZ(player.getZ() + (player.getZ() < z ? Math.min(speed, z - player.getZ()) : Math.max(-speed, z - player.getZ())));

			if(player.getX() == x && player.getY() == y && player.getZ() == z) {
				nextStep = nextStep.getNext();
				ticksSinceStepChange = 0;
			}
		}
	}

	@Override
	public void stop() {
		if(thread != null && !thread.isDone())
			thread.cancel(true);
		nextStep = null;
	}
	public boolean isMoving() {
		return nextStep != null;
	}

	@Override
	public boolean isActive() {
		return thread != null || nextStep != null;
	}

	public static double getDefaultSpeed() {
		return defaultSpeed;
	}

	public static void setDefaultSpeed(double defaultSpeed) {
		WalkActivity.defaultSpeed = defaultSpeed;
	}

	public static double getDefaultJumpFactor() {
		return defaultJumpFactor;
	}

	public static void setDefaultJumpFactor(double defaultJumpFactor) {
		WalkActivity.defaultJumpFactor = defaultJumpFactor;
	}

	public static double getDefaultFallFactor() {
		return defaultFallFactor;
	}

	public static void setDefaultFallFactor(double defaultFallFactor) {
		WalkActivity.defaultFallFactor = defaultFallFactor;
	}

	public static double getDefaultLiquidFactor() {
		return defaultLiquidFactor;
	}

	public static void setDefaultLiquidFactor(double defaultLiquidFactor) {
		WalkActivity.defaultLiquidFactor = defaultLiquidFactor;
	}

	public static int getDefaultTimeout() {
		return defaultTimeout;
	}

	public static void setDefaultTimeout(int defaultTimeout) {
		WalkActivity.defaultTimeout = defaultTimeout;
	}
}
