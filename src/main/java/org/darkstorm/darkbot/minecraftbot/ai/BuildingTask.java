package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.event.EventListener;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class BuildingTask implements Task, EventListener {
	private static final boolean[] UNPLACEABLE = new boolean[256];
	static {
		UNPLACEABLE[0] = true;
		UNPLACEABLE[8] = true;
		UNPLACEABLE[9] = true;
		UNPLACEABLE[10] = true;
		UNPLACEABLE[11] = true;
		UNPLACEABLE[26] = true;
		UNPLACEABLE[31] = true;
		UNPLACEABLE[51] = true;
		UNPLACEABLE[54] = true;
		UNPLACEABLE[61] = true;
		UNPLACEABLE[62] = true;
		UNPLACEABLE[64] = true;
		UNPLACEABLE[69] = true;
		UNPLACEABLE[71] = true;
		UNPLACEABLE[77] = true;
		UNPLACEABLE[78] = true;
		UNPLACEABLE[96] = true;
		UNPLACEABLE[107] = true;
	}

	private final MinecraftBot bot;
	private EatTask eatTask;
	private boolean running = false;

	private int ticksWait, blockId;
	private BlockLocation startCounterLocation = null;
	private BlockLocation point1, point2;

	public BuildingTask(final MinecraftBot bot) {
		this.bot = bot;
		bot.getEventBus().register(this);
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return running;
	}

	@Override
	public synchronized boolean start(String... options) {
		TaskManager taskManager = bot.getTaskManager();
		eatTask = taskManager.getTaskFor(EatTask.class);
		blockId = Integer.parseInt(options[0]);
		point1 = new BlockLocation(Integer.parseInt(options[1]), Integer.parseInt(options[2]), Integer.parseInt(options[3]));
		point2 = new BlockLocation(Integer.parseInt(options[4]), Integer.parseInt(options[5]), Integer.parseInt(options[6]));
		BlockLocation temp = point1;
		point1 = point1.getY() <= point2.getY() ? point1 : point2;
		point2 = temp.getY() > point2.getY() ? temp : point2;

		running = true;
		return true;
	}

	@Override
	public synchronized void stop() {
		running = false;
		ticksWait = 0;
	}

	@Override
	public synchronized void run() {
		if(eatTask.isActive())
			return;
		if(ticksWait > 0) {
			ticksWait--;
			return;
		}
		World world = bot.getWorld();
		MainPlayerEntity player = bot.getPlayer();
		System.out.println("Building!");
		BlockLocation ourLocation = new BlockLocation(player.getLocation());
		boolean flip = false;
		for(int y = point1.getY(); y <= point2.getY(); y++) {
			for(int x = Math.min(point1.getX(), point2.getX()); x <= Math.max(point1.getX(), point2.getX()); x++) {
				int z1 = Math.min(point1.getZ(), point2.getZ());
				int z2 = Math.max(point1.getZ(), point2.getZ());
				if(flip) {
					int temp = z1;
					z1 = z2;
					z2 = temp;
				}
				for(int z = z1; flip ? z >= z2 : z <= z2; z += flip ? -1 : 1) {
					if(startCounterLocation != null && startCounterLocation.getX() == x && startCounterLocation.getY() == y && startCounterLocation.getZ() == z)
						continue;
					int id = world.getBlockIdAt(x, y, z);
					if(!BlockType.getById(id).isSolid()) {
						BlockLocation location = new BlockLocation(x, y, z);
						if(startCounterLocation != null) {
							System.out.println(ourLocation + " -> " + location);
							BlockLocation original = location;
							BlockLocation below = location.offset(0, -1, 0);
							while(!BlockType.getById(world.getBlockIdAt(below)).isSolid() && !world.getPathFinder().getWorldPhysics().canClimb(below)) {
								location = below;
								below = below.offset(0, -1, 0);
								if(original.getY() - location.getY() >= 5)
									return;
							}
							if(!location.equals(ourLocation)) {
								System.out.println("walking...");
								bot.setActivity(new WalkActivity(bot, location));
								ticksWait = 4;
								return;
							}
							int index = player.getInventory().getFirstSlot(blockId);
							if(index == -1) {
								System.out.println("Stopping! No blocks left");
								stop();
								return;
							}
							System.out.println("Placing block.");
							player.switchHeldItems(index);
							player.placeBlock(startCounterLocation);

							startCounterLocation = null;
						}

						startCounterLocation = location;
					}
				}
				flip = !flip;
			}
		}
	}

	@Override
	public synchronized boolean isActive() {
		return running;
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
		return "Build";
	}

	@Override
	public String getOptionDescription() {
		return "<id> <x1> <y1> <z1> <x2> <y2> <z2>";
	}
}
