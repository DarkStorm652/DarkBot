package org.darkstorm.minecraft.darkbot.ai;

import java.util.Arrays;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.*;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.*;

public class DestroyingTask implements Task, EventListener {
	private static final BlockLocation[] surrounding = new BlockLocation[] { new BlockLocation(-1, 0, 1), new BlockLocation(0, 0, 1), new BlockLocation(1, 0, 1), new BlockLocation(-1, 0, 0), new BlockLocation(1, 0, 0), new BlockLocation(-1, 0, -1), new BlockLocation(0, 0, -1), new BlockLocation(1, 0, -1), };

	private final MinecraftBot bot;
	private EatTask eatTask;
	private boolean running = false;

	private int ticksWait;
	private BlockLocation lastPlacement, nextMove;
	private BlockLocation point1, point2;

	public DestroyingTask(final MinecraftBot bot) {
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
		point1 = new BlockLocation(Integer.parseInt(options[0]), Integer.parseInt(options[1]), Integer.parseInt(options[2]));
		point2 = new BlockLocation(Integer.parseInt(options[3]), Integer.parseInt(options[4]), Integer.parseInt(options[5]));
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
		System.out.println("Destroying!");
		BlockLocation ourLocation = new BlockLocation(player.getLocation());
		boolean flip = false, next = false;
		for(int x = Math.min(point1.getX(), point2.getX()); x <= Math.max(point1.getX(), point2.getX()); x++) {
			int z1 = Math.min(point1.getZ(), point2.getZ());
			int z2 = Math.max(point1.getZ(), point2.getZ());
			if(flip) {
				int temp = z1;
				z1 = z2;
				z2 = temp;
			}
			for(int z = z1; flip ? z >= z2 : z <= z2; z += flip ? -1 : 1) {
				if(next) {
					nextMove = new BlockLocation(x, point1.getY(), z);
					next = false;
				}
				for(int y = point1.getY(); y <= point2.getY(); y++) {
					BlockLocation location = new BlockLocation(x, y, z);
					int id = world.getBlockIdAt(location);
					if(!BlockType.getById(id).isSolid())
						continue;
					if(player.getDistanceToSquared(location) > 16) {
						BlockLocation[] newSurrounding = Arrays.copyOf(surrounding, surrounding.length);
						for(int i = 0; i < newSurrounding.length; i++) {
							BlockLocation adjacent = newSurrounding[i];
							newSurrounding[i] = new BlockLocation(x + adjacent.getX(), y + adjacent.getY(), z + adjacent.getZ());
						}
						for(BlockLocation newLocation : newSurrounding) {
							if(!BlockType.getById(world.getBlockIdAt(newLocation.getX(), newLocation.getY(), newLocation.getZ())).isSolid()) {
								int yChange = 0;
								while(!BlockType.getById(world.getBlockIdAt(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ())).isSolid() && newLocation.getY() >= 0 && yChange < 5) {
									newLocation = new BlockLocation(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ());
									yChange++;
								}
								if(yChange == 5 || BlockType.getById(world.getBlockIdAt(newLocation.getX(), newLocation.getY() + 1, newLocation.getZ())).isSolid())
									continue;
								bot.setActivity(new WalkActivity(bot, newLocation));
								return;
							}
						}
					}
					if(player.breakBlock(location)) {
						nextMove = null;
						return;
					}
				}

				if(nextMove != null) {
					BlockType below = BlockType.getById(world.getBlockIdAt(nextMove.offset(0, -1, 0)));
					if(!below.isSolid())
						if(below.isPlaceable() && !below.isIndestructable()) {
							player.breakBlock(nextMove.offset(0, -1, 0));
							return;
						} else
							placeBlockAt(nextMove.offset(0, -1, 0));
					bot.setActivity(new WalkActivity(bot, nextMove));
					ticksWait = 4;
					nextMove = null;
					return;
				}
				if(x == ourLocation.getX() && z == ourLocation.getZ())
					next = true;
			}
			flip = !flip;
		}
		stop();
	}

	private boolean placeBlockAt(BlockLocation location) {
		if(lastPlacement != null && lastPlacement.equals(location)) {
			lastPlacement = null;
			return false;
		}
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		PlayerInventory inventory = player.getInventory();
		int slot = -1;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item == null)
				continue;
			int id = item.getId();
			if(id == 1 || id == 3 || id == 4) {
				slot = i;
				break;
			}
		}
		if(slot == -1)
			return false;
		if(!player.switchHeldItems(slot))
			return false;
		if(player.placeBlock(location)) {
			lastPlacement = location;
			return true;
		}
		return false;
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
		return "Destroy";
	}

	@Override
	public String getOptionDescription() {
		return "<x1> <y1> <z1> <x2> <y2> <z2>";
	}
}
