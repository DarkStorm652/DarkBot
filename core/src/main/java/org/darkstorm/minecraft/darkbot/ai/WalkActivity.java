package org.darkstorm.darkbot.minecraftbot.ai;

import java.util.Set;
import java.util.concurrent.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.pathfinding.*;

public class WalkActivity implements Activity {
	private static double defaultSpeed = 0.17, defaultJumpFactor = 3, defaultFallFactor = 4, defaultLiquidFactor = 0.5;
	private static int defaultTimeout = 60000;

	private final MinecraftBot bot;
	private final ExecutorService service = Executors.newSingleThreadExecutor();
	private final BlockLocation target;

	private final long startTime;

	private final Future<PathNode> thread;
	private boolean searchCompleted;
	
	private PathNode nextStep;
	private WorldLocation stepTarget;
	private int ticksSinceStepChange = 0;
	private int timeout = defaultTimeout;
	private double speed = defaultSpeed, jumpFactor = defaultJumpFactor, fallFactor = defaultFallFactor, liquidFactor = defaultLiquidFactor;

	public WalkActivity(MinecraftBot bot, BlockLocation target) {
		this(bot, target, false);
	}

	public WalkActivity(final MinecraftBot bot, final BlockLocation target, boolean keepWalking) {
		this.bot = bot;
		this.target = target;
		System.out.println("Generating path...");
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
				while(!search.isDone() && !thread.isCancelled())
					search.step();
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
	 * Walk speed, in blocks/tick. Default is 0.17.
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
		if(!thread.isDone()) {
			if(timeout > 0 && System.currentTimeMillis() - startTime > timeout) {
				thread.cancel(true);
				setNextStep(null);
			} else {
				MainPlayerEntity player = bot.getPlayer();
				player.setYaw((player.getYaw() + 15) % 360);
			}
			return;
		} else if(thread.isDone() && !thread.isCancelled() && !searchCompleted) {
			searchCompleted = true;
			try {
				setNextStep(thread.get());
				if(nextStep == null) {
					System.out.println("Error! Could not find path!");
					return;
				} else
					System.out.println("Path found, walking...");
			} catch(Exception exception) {
				exception.printStackTrace();
				setNextStep(null);
				return;
			}
		} else if(thread.isCancelled())
			return;
		
		searchCompleted = true;
		if(nextStep != null) {
			MainPlayerEntity player = bot.getPlayer();
			System.out.println(" -> Moving from " + player.getLocation() + " to " + nextStep);
			if(player.getDistanceToSquared(stepTarget) > 4) {
				nextStep = null;
				return;
			}
			ticksSinceStepChange++;
			if(ticksSinceStepChange > 80 || player.getWorld().isColliding(player.getBoundingBox())) {
				nextStep = null;
				return;
			}
			
			WorldLocation nextStepTarget = findNextStepTarget();
			if(player.getDistanceTo(stepTarget.getX(), Math.abs(player.getY() - stepTarget.getY()) < 1 ? player.getY() : stepTarget.getY(), stepTarget.getZ()) < 0.3
					|| (nextStepTarget != null && player.getDistanceTo(nextStepTarget) < stepTarget.getDistanceTo(nextStepTarget))) {
				setNextStep(nextStep.getNext());
				if(nextStep == null)
					return;
				stepTarget = nextStepTarget;
				nextStepTarget = findNextStepTarget();
			}

			double x = stepTarget.getX(), y = stepTarget.getY(), z = stepTarget.getZ();
			//System.out.println("TARGETING [" + x + "," + y + "," + z + "]");
			double dist = Math.hypot(x - player.getX(), z - player.getZ());
			if(dist < 0.5 && nextStepTarget == null) {
				player.setVelocityX(0);
				player.setVelocityZ(0);
				player.accelerate(Math.atan2(z - player.getZ(), x - player.getX()), 0, Math.min(dist / 4, speed / 4), Math.min(dist, speed));
			} else
				player.accelerate(Math.atan2(z - player.getZ(), x - player.getX()), 0, speed / 4, Math.min(dist, speed));
			if(y > player.getY() && (y - player.getY() > 0.5 || dist > 1.5 || !player.getWorld().isColliding(player.getBoundingBoxAt(stepTarget.getX(), stepTarget.getY() - 1, stepTarget.getZ()))))
				player.jump();
			
		}
	}
	
	private WorldLocation findNextStepTarget() {
		MainPlayerEntity player = bot.getPlayer();
		WorldLocation nextStepTarget = getNextStep(1);
		if(nextStepTarget != null && nextStepTarget.getY() > stepTarget.getY() && nextStepTarget.getX() == stepTarget.getX() && nextStepTarget.getY() == stepTarget.getY() && !player.getWorld().isInMaterial(player.getBoundingBoxAt(nextStepTarget.getX() + 0.5, nextStepTarget.getY(), nextStepTarget.getZ() + 0.5), BlockType.WATER, BlockType.STATIONARY_WATER, BlockType.LAVA, BlockType.STATIONARY_LAVA, BlockType.LADDER, BlockType.VINE))
			nextStepTarget = getNextStep(2);
		
		if(nextStepTarget != null) {
			BoundingBox bounds = player.getBoundingBox();
			if(nextStepTarget.getY() > player.getY() && nextStepTarget.getY() - player.getY() > 0.5) {
				if(!collides(player.getWorld(), player.getBoundingBoxAt(player.getX() + (nextStepTarget.getX() - player.getX()) / 2, nextStepTarget.getY() - 0.5, player.getZ() + (nextStepTarget.getZ() - player.getZ()) / 2), bounds)) {
					WorldLocation nextNextStepTarget = getNextStep(2);
					if(nextNextStepTarget != null && nextNextStepTarget.getY() == nextStepTarget.getY() && !collides(player.getWorld(), player.getBoundingBoxAt(nextNextStepTarget.getX(), nextStepTarget.getY() - 0.5, nextNextStepTarget.getZ()), bounds)) {
						nextStepTarget = new WorldLocation(nextNextStepTarget.getX(), nextStepTarget.getY() - 0.5, nextNextStepTarget.getZ());
					}
				}
			}
		}
		return nextStepTarget;
	}
	
	private boolean collides(World world, BoundingBox target, BoundingBox current) {
		Set<Block> blocks = world.getCollidingBlocks(target);
		blocks.removeAll(world.getCollidingBlocks(current));
		return !blocks.isEmpty();
	}
	
	private WorldLocation getNextStep(int lookahead) {
		PathNode nextStep = this.nextStep;
		for(int i = 0; i < lookahead && nextStep != null; i++)
			nextStep = nextStep.getNext();
		
		if(nextStep == null)
			return null;
		return new WorldLocation(nextStep.getLocation());
	}
	private void setNextStep(PathNode step) {
		nextStep = step;
		stepTarget = nextStep != null ? new WorldLocation(nextStep.getLocation()) : null;
		ticksSinceStepChange = 0;
	}

	@Override
	public void stop() {
		if(!thread.isDone())
			thread.cancel(true);
		nextStep = null;
	}
	public boolean isMoving() {
		return nextStep != null;
	}

	@Override
	public boolean isActive() {
		return !thread.isCancelled() && (!searchCompleted || nextStep != null);
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
