package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.event.EventListener;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class MiningTask implements Task, EventListener {
	private static final boolean[] ORES = new boolean[256];
	private static final int TUNNEL_LENGTH = 35;

	static {
		ORES[14] = true;
		ORES[15] = true;
		ORES[16] = true;
		ORES[21] = true;
		ORES[56] = true;
		ORES[73] = true;
		ORES[74] = true;
	}

	private final MinecraftBot bot;

	private EatTask eatTask;
	private boolean running = false;

	private BlockLocation previous, nextTarget;
	private int xStart = Integer.MAX_VALUE, zStart = Integer.MAX_VALUE, zDirection, ticksWait, skipForward;
	private BlockLocation lastLocation, lastPlacement;

	public MiningTask(final MinecraftBot bot) {
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
		running = true;
		return true;
	}

	@Override
	public synchronized void stop() {
		running = false;
		nextTarget = null;
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
		if(nextTarget != null && player.getDistanceTo(nextTarget) < 3.5) {
			int id = world.getBlockIdAt(nextTarget);
			if(id == 0 || id == 8 || id == 9 || id == 10 || id == 11) {
				nextTarget = checkSurrounding(nextTarget);
				return;
			}
			BlockLocation target = nextTarget;
			nextTarget = null;
			breakBlock(target);
			return;
		} else if(nextTarget != null && player.getDistanceTo(nextTarget) > 8)
			nextTarget = null;
		System.out.println("Mining!");
		BlockLocation ourLocation = new BlockLocation(player.getLocation());
		checkTorches(ourLocation);
		if(BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY() + 1, ourLocation.getZ())).isSolid()) {
			breakBlock(new BlockLocation(ourLocation.getX(), ourLocation.getY() + 1, ourLocation.getZ()));
			return;
		} else if(BlockType.getById(world.getBlockIdAt(ourLocation)).isSolid()) {
			breakBlock(ourLocation);
			return;
		}
		BlockLocation newLocation = null;
		if(ourLocation.getY() < 11) {
			skipForward = 0;
			for(int offset = 1; offset < Math.max(11 - ourLocation.getY(), 3); offset++) {
				newLocation = ourLocation.offset(offset == 1 ? -1 : 0, offset == 1 ? 2 : offset - 1, 0);
				if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
					breakBlock(newLocation);
					return;
				}
			}
			int belowId = world.getBlockIdAt(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ());
			if(belowId == 0 || belowId == 8 || belowId == 9 || belowId == 10 || belowId == 11)
				placeBlockAt(newLocation.offset(0, -1, 0));
			player.face(newLocation.getX(), newLocation.getY() + 1, newLocation.getZ());
			bot.setActivity(new WalkActivity(bot, newLocation));
		} else if(ourLocation.getY() > 11) {
			if(skipForward == 0) {
				for(int offset = 1; offset >= -1; offset--) {
					newLocation = new BlockLocation(ourLocation.getX() - 1, ourLocation.getY() + offset, ourLocation.getZ());
					if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
						breakBlock(newLocation);
						return;
					}
				}
			} else {
				bot.setActivity(new WalkActivity(bot, ourLocation.offset(-1, 0, 0)));
				skipForward = 0;
				return;
			}
			int belowId = world.getBlockIdAt(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ());
			BlockType belowType = BlockType.getById(belowId);
			if(belowType == BlockType.AIR || !belowType.isSolid()) {
				if(!belowType.isIndestructable() && belowType.isPlaceable()) {
					breakBlock(newLocation.offset(0, -1, 0));
					return;
				}
				boolean success = placeBlockAt(newLocation.offset(0, -1, 0));
				if(!success) {
					BlockLocation originalLocation = newLocation;
					BlockLocation locationOffset = newLocation.offset(0, -1, 0);
					while(!BlockType.getById(world.getBlockIdAt(locationOffset)).isSolid()) {
						newLocation = locationOffset;
						if(originalLocation.getY() - locationOffset.getY() > 5) {
							if(placeBlockAt(originalLocation))
								skipForward++;
							return;
						}
						locationOffset = locationOffset.offset(0, -1, 0);
					}
				} else {
					ticksWait = 4;
					return;
				}
			}
			player.face(newLocation.getX(), newLocation.getY() + 1, newLocation.getZ());
			bot.setActivity(new WalkActivity(bot, newLocation));
		} else {
			skipForward = 0;
			if(ourLocation.getX() % 3 == 0) {
				if(xStart != ourLocation.getX()) {
					xStart = ourLocation.getX();
					zStart = ourLocation.getZ();
				}
				if(zStart != ourLocation.getZ()) {
					int zOffset = ourLocation.getZ() - zStart;
					if(Math.abs(zOffset) >= TUNNEL_LENGTH) {
						if(zOffset > 0 && zDirection > 0 || zOffset < 0 && zDirection < 0)
							zDirection *= -1;
					}
					for(int offset = 1; offset >= 0; offset--) {
						newLocation = new BlockLocation(ourLocation.getX(), ourLocation.getY() + offset, ourLocation.getZ() + zDirection);
						if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
							breakBlock(newLocation);
							return;
						}
					}
					if(zStart != ourLocation.getZ() + zDirection) {
						for(int zFactor = 2; zFactor >= 1; zFactor--) {
							for(int offset = 0; offset <= 1; offset++) {
								BlockLocation otherLocation = new BlockLocation(ourLocation.getX(), ourLocation.getY() + offset, ourLocation.getZ() + zDirection * zFactor);
								BlockType type = BlockType.getById(world.getBlockIdAt(otherLocation));
								if(type == BlockType.LAVA || type == BlockType.STATIONARY_LAVA) {
									if(placeBlockAt(otherLocation))
										return;
								}
							}
						}
						for(int xOffset = 1; xOffset >= -1; xOffset--) {
							if(xOffset != 0) {
								for(int offset = 1; offset >= 0; offset--) {
									BlockLocation otherLocation = new BlockLocation(ourLocation.getX() + xOffset, ourLocation.getY() + offset, ourLocation.getZ() + zDirection);
									BlockType type = BlockType.getById(world.getBlockIdAt(otherLocation));
									if(type == BlockType.AIR || (!type.isSolid() && !type.isPlaceable())) {
										if(placeBlockAt(otherLocation))
											return;
									}
								}
								continue;
							}
							BlockLocation otherLocation = new BlockLocation(ourLocation.getX(), ourLocation.getY() + 2, ourLocation.getZ() + zDirection);
							BlockType type = BlockType.getById(world.getBlockIdAt(otherLocation));
							if(type == BlockType.AIR || (!type.isSolid() && !type.isPlaceable())) {
								if(placeBlockAt(otherLocation))
									return;
							}
						}
					}
				} else {
					if(BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY(), ourLocation.getZ() + 1)).isSolid() || BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY() + 1, ourLocation.getZ() + 1)).isSolid() || BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY(), ourLocation.getZ() + 2)).isSolid() || BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY() + 1, ourLocation.getZ() + 2)).isSolid())
						zDirection = 1;
					else if(BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY(), ourLocation.getZ() - 1)).isSolid() || BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY() + 1, ourLocation.getZ() - 1)).isSolid() || BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY(), ourLocation.getZ() - 2)).isSolid() || BlockType.getById(world.getBlockIdAt(ourLocation.getX(), ourLocation.getY() + 1, ourLocation.getZ() - 2)).isSolid())
						zDirection = -1;
					else {
						for(int offset = 1; offset >= 0; offset--) {
							newLocation = new BlockLocation(ourLocation.getX() - 1, ourLocation.getY() + offset, ourLocation.getZ());
							if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
								breakBlock(newLocation);
								return;
							}
						}
						int belowId = world.getBlockIdAt(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ());
						if(belowId == 0 || belowId == 8 || belowId == 9 || belowId == 10 || belowId == 11)
							placeBlockAt(new BlockLocation(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ()));
						player.face(newLocation.getX(), newLocation.getY() + 1, newLocation.getZ());
						bot.setActivity(new WalkActivity(bot, newLocation));
						return;
					}
					for(int offset = 1; offset >= 0; offset--) {
						newLocation = new BlockLocation(ourLocation.getX(), ourLocation.getY() + offset, ourLocation.getZ() + zDirection);
						if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
							breakBlock(newLocation);
							return;
						}
					}
				}
			} else {
				for(int offset = 1; offset >= 0; offset--) {
					newLocation = new BlockLocation(ourLocation.getX() - 1, ourLocation.getY() + offset, ourLocation.getZ());
					if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
						breakBlock(newLocation);
						return;
					}
				}
			}
			int belowId = world.getBlockIdAt(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ());
			if(belowId == 0 || belowId == 8 || belowId == 9 || belowId == 10 || belowId == 11)
				placeBlockAt(new BlockLocation(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ()));
			player.face(newLocation.getX(), newLocation.getY() + 1, newLocation.getZ());
			bot.setActivity(new WalkActivity(bot, newLocation));
		}
	}

	@Override
	public synchronized boolean isActive() {
		return running;
	}

	private void breakBlock(BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null)
			return;
		int face = getBreakBlockFaceAt(location);
		if(face == -1)
			return;
		player.face(x, y, z);
		int idAbove = world.getBlockIdAt(x, y + 1, z);
		if(idAbove == 12 || idAbove == 13) {
			ticksWait = 30;
			nextTarget = location;
		} else if(nextTarget == null && previous != null)
			nextTarget = checkSurrounding(previous);
		player.breakBlock(location);
		previous = location;
	}

	private int getBreakBlockFaceAt(BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		World world = bot.getWorld();
		if(isEmpty(world.getBlockIdAt(x - 1, y, z))) {
			return 4;
		} else if(isEmpty(world.getBlockIdAt(x, y, z + 1))) {
			return 3;
		} else if(isEmpty(world.getBlockIdAt(x, y, z - 1))) {
			return 2;
		} else if(isEmpty(world.getBlockIdAt(x, y + 1, z))) {
			return 1;
		} else if(isEmpty(world.getBlockIdAt(x, y - 1, z))) {
			return 0;
		} else if(isEmpty(world.getBlockIdAt(x + 1, y, z))) {
			return 5;
		} else
			return -1;
	}

	private boolean isEmpty(int id) {
		BlockType type = BlockType.getById(id);
		return !type.isSolid() && !type.isInteractable() && !type.isPlaceable();
	}

	private BlockLocation checkSurrounding(BlockLocation location) {
		World world = bot.getWorld();
		int x = location.getX(), y = location.getY(), z = location.getZ();
		if(ORES[world.getBlockIdAt(x - 1, y, z)]) {
			x--;
		} else if(ORES[world.getBlockIdAt(x + 1, y, z)]) {
			x++;
		} else if(ORES[world.getBlockIdAt(x, y - 1, z)]) {
			y--;
		} else if(ORES[world.getBlockIdAt(x, y + 1, z)]) {
			y++;
		} else if(ORES[world.getBlockIdAt(x, y, z - 1)]) {
			z--;
		} else if(ORES[world.getBlockIdAt(x, y, z + 1)]) {
			z++;
		} else if(ORES[world.getBlockIdAt(x - 1, y, z - 1)]) {
			x--;
		} else if(ORES[world.getBlockIdAt(x - 1, y, z + 1)]) {
			x--;
		} else if(ORES[world.getBlockIdAt(x + 1, y, z - 1)]) {
			x++;
		} else if(ORES[world.getBlockIdAt(x + 1, y, z + 1)]) {
			x++;
		} else if(ORES[world.getBlockIdAt(x - 1, y - 1, z)]) {
			x--;
		} else if(ORES[world.getBlockIdAt(x - 1, y + 1, z)]) {
			x--;
		} else if(ORES[world.getBlockIdAt(x + 1, y - 1, z)]) {
			x++;
		} else if(ORES[world.getBlockIdAt(x + 1, y + 1, z)]) {
			x++;
		} else
			return null;
		return new BlockLocation(x, y, z);
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

	private void checkTorches(BlockLocation ourLocation) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		if(lastLocation == null || ((ourLocation.getX() == lastLocation.getX() || Math.abs(ourLocation.getX() % 6) != 1) && (ourLocation.getZ() == lastLocation.getZ() || Math.abs(ourLocation.getZ() % 6) != 1))) {
			lastLocation = ourLocation;
			return;
		}
		lastLocation = ourLocation;
		PlayerInventory inventory = player.getInventory();

		inventory.close();
		while(!inventory.contains(50)) {
			if(!inventory.contains(263)) {
				inventory.close();
				return;
			}
			if(inventory.contains(280)) {
				inventory.selectItemAt(inventory.getFirstSlot(263));
				inventory.selectCraftingAt(0);
				inventory.selectItemAt(inventory.getFirstSlot(280));
				inventory.selectCraftingAt(2);
				inventory.setCraftingOutput(new BasicItemStack(50, 4, 0));
				inventory.selectCraftingOutput();
				inventory.selectItemAt(inventory.getFirstSlot(0));
				ItemStack item = inventory.getCraftingAt(0);
				if(item != null) {
					inventory.selectCraftingAt(0);
					inventory.selectItemAt(inventory.getFirstSlot(0));
				}
				item = inventory.getCraftingAt(2);
				if(item != null) {
					inventory.selectCraftingAt(2);
					inventory.selectItemAt(inventory.getFirstSlot(0));
				}
				break;
			} else {
				inventory.close();
				return;
			}
		}
		inventory.close();

		int slot = -1;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item == null)
				continue;
			if(item.getId() == 50) {
				slot = i;
				break;
			}
		}
		if(slot == -1)
			return;
		if(!player.switchHeldItems(slot))
			return;

		player.placeBlock(new BlockLocation(ourLocation.getX(), ourLocation.getY() + 1, ourLocation.getZ()));
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
		return "Mine";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
