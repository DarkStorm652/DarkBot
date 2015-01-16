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

	private Future<PathNode> thread;
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
				while(!search.isDone() && (thread == null || !thread.isCancelled()))
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
		if(thread != null && !thread.isDone()) {
			if(timeout > 0 && System.currentTimeMillis() - startTime > timeout) {
				thread.cancel(true);
				thread = null;
				setNextStep(null);
				return;
			}
		} else if(thread != null && thread.isDone() && !thread.isCancelled()) {
			try {
				setNextStep(thread.get());
				System.out.println("Path found, walking...");
			} catch(Exception exception) {
				exception.printStackTrace();
				setNextStep(null);
				return;
			} finally {
				thread = null;
			}
		}
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
			

			WorldLocation nextStepTarget = getNextStep(1);
			if(nextStepTarget != null && nextStepTarget.getY() > stepTarget.getY() && nextStepTarget.getX() == stepTarget.getX() && nextStepTarget.getY() == stepTarget.getY() && !player.getWorld().isInMaterial(player.getBoundingBoxAt(nextStepTarget.getX() + 0.5, nextStepTarget.getY(), nextStepTarget.getZ() + 0.5), BlockType.WATER, BlockType.STATIONARY_WATER, BlockType.LAVA, BlockType.STATIONARY_LAVA, BlockType.LADDER, BlockType.VINE))
				nextStepTarget = getNextStep(2);
			
			if(nextStepTarget != null) {
				BoundingBox bounds = player.getBoundingBox();
				if(nextStepTarget.getY() > player.getY() && nextStepTarget.getY() - player.getY() > 0.5) {
					if(!collides(player.getWorld(), player.getBoundingBoxAt(nextStepTarget.getX(), nextStepTarget.getY() - 0.5, nextStepTarget.getZ()), bounds)) {
						WorldLocation nextNextStepTarget = getNextStep(2);
						if(nextNextStepTarget != null && nextNextStepTarget.getY() == nextStepTarget.getY() && !collides(player.getWorld(), player.getBoundingBoxAt(nextNextStepTarget.getX(), nextStepTarget.getY() - 0.5, nextNextStepTarget.getZ()), bounds)) {
							nextStepTarget = new WorldLocation(nextNextStepTarget.getX(), nextStepTarget.getY() - 0.5, nextNextStepTarget.getZ());
						}
					}
				}
			}
			
			if(player.getDistanceTo(stepTarget.getX(), Math.abs(player.getY() - stepTarget.getY()) < 1 ? player.getY() : stepTarget.getY(), stepTarget.getZ()) < 0.3
					|| (nextStepTarget != null && player.getDistanceTo(nextStepTarget) < stepTarget.getDistanceTo(nextStepTarget))) {
				setNextStep(nextStep.getNext());
				if(nextStep == null)
					return;
				stepTarget = nextStepTarget;
			}

			//System.out.println("   ::> Targeting " + stepTarget + " [" + player.isOnGround() + "]");
			double x = stepTarget.getX(), y = stepTarget.getY(), z = stepTarget.getZ();
			player.accelerate(Math.atan2(z - player.getZ(), x - player.getX()), 0, speed / 4, Math.min(player.getDistanceTo(stepTarget), speed));
			if(y != player.getY() && player.isInMaterial(BlockType.WATER, BlockType.STATIONARY_WATER, BlockType.LAVA, BlockType.STATIONARY_LAVA, BlockType.LADDER, BlockType.VINE))
				player.accelerate(0, Math.signum(y - player.getY()) * Math.PI / 2, 0.1, Math.min(Math.abs(y - player.getY()), 0.15));
			else if(y - player.getY() > 0.5 && player.isOnGround())
				player.accelerate(0, Math.PI / 2, 0.42, 0.42);
			
		}
	}
	
	private boolean collides(World world, BoundingBox target, BoundingBox current) {
		Set<Block> blocks = world.getCollidingBlocks(target);
		blocks.removeAll(world.getCollidingBlocks(current));
		return !blocks.isEmpty();
	}

	private double moveToward(double current, double target, double speed) {
		if(current < target)
			return current + Math.min(speed, target - current);
		else if(current > target)
			return current + Math.max(-speed, target - current);
		return current;
	}

	private boolean checkOver(World world, WorldLocation location, BlockType type, int data) {
		BlockLocation block = new BlockLocation(location);
		double offX = location.getX() - (block.getX() + 0.5), offY = location.getY() - block.getY(), offZ = location.getZ() - (block.getZ() + 0.5);
		if(offY > 0.25)
			block = block.offset(0, 1, 0);

		boolean valid = typeMatches(world, block.getX(), block.getY() - 1, block.getZ(), type, data);
		if(offX > 0.2) {
			if(offZ > 0.2)
				valid = valid && typeMatches(world, block.getX() + 1, block.getY() - 1, block.getZ() + 1, type, data);
			else if(offZ < -0.2)
				valid = valid && typeMatches(world, block.getX() + 1, block.getY() - 1, block.getZ() - 1, type, data);
			valid = valid && typeMatches(world, block.getX() + 1, block.getY() - 1, block.getZ(), type, data);
		} else if(offX < -0.2) {
			if(offZ > 0.2)
				valid = valid && typeMatches(world, block.getX() - 1, block.getY() - 1, block.getZ() + 1, type, data);
			else if(offZ < -0.2)
				valid = valid && typeMatches(world, block.getX() - 1, block.getY() - 1, block.getZ() - 1, type, data);
			valid = valid && typeMatches(world, block.getX() - 1, block.getY() - 1, block.getZ(), type, data);
		}
		if(offZ > 0.2)
			valid = valid && typeMatches(world, block.getX(), block.getY() - 1, block.getZ() + 1, type, data);
		else if(offZ < -0.2)
			valid = valid && typeMatches(world, block.getX(), block.getY() - 1, block.getZ() - 1, type, data);

		return valid;
	}

	private boolean typeMatches(World world, int x, int y, int z, BlockType type, int data) {
		if(type != BlockType.getById(world.getBlockIdAt(x, y, z)))
			return false;
		return data == -1 || data == world.getBlockMetadataAt(x, y, z);
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
