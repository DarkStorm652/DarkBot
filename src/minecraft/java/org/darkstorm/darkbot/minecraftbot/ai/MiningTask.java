package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.world.BlockChangeEvent;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class MiningTask implements Task, EventListener {
	private static final boolean[] MINEABLE = new boolean[256],
			DIGGABLE = new boolean[256], UNPLACEABLE = new boolean[256],
			ORES = new boolean[256];
	@SuppressWarnings("unused")
	private static final int[] PICKAXES = new int[3200],
			SHOVELS = new int[3200], DROPPABLE = new int[3200];
	private static final int TUNNEL_LENGTH = 35;

	static {
		MINEABLE[1] = true;
		MINEABLE[14] = true;
		MINEABLE[15] = true;
		MINEABLE[16] = true;
		MINEABLE[21] = true;
		MINEABLE[24] = true;
		MINEABLE[43] = true;
		MINEABLE[44] = true;
		MINEABLE[45] = true;
		MINEABLE[48] = true;
		MINEABLE[52] = true;
		MINEABLE[56] = true;
		MINEABLE[57] = true;
		MINEABLE[61] = true;
		MINEABLE[62] = true;
		MINEABLE[67] = true;
		MINEABLE[73] = true;
		MINEABLE[74] = true;
		MINEABLE[87] = true;
		MINEABLE[89] = true;

		DIGGABLE[2] = true;
		DIGGABLE[3] = true;
		DIGGABLE[12] = true;
		DIGGABLE[13] = true;
		DIGGABLE[60] = true;
		DIGGABLE[88] = true;
		DIGGABLE[110] = true;

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

		ORES[14] = true;
		ORES[15] = true;
		ORES[16] = true;
		ORES[21] = true;
		ORES[56] = true;
		ORES[73] = true;
		ORES[74] = true;

		PICKAXES[257] = 4;
		PICKAXES[270] = 1;
		PICKAXES[274] = 2;
		PICKAXES[278] = 5;
		PICKAXES[285] = 3;

		SHOVELS[256] = 4;
		SHOVELS[269] = 1;
		SHOVELS[273] = 2;
		SHOVELS[277] = 5;
		SHOVELS[284] = 3;
	}

	private final MinecraftBot bot;

	private WalkTask walkTask;
	private EatTask eatTask;
	private boolean running = false;

	private BlockLocation currentlyBreaking, previous, nextTarget;
	private int ticksSinceBreak, xStart = Integer.MAX_VALUE,
			zStart = Integer.MAX_VALUE, zDirection, ticksWait;
	private BlockLocation lastLocation;

	public MiningTask(final MinecraftBot bot) {
		this.bot = bot;
		bot.getEventManager().registerListener(this);
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return running;
	}

	@Override
	public synchronized boolean start(String... options) {
		TaskManager taskManager = bot.getTaskManager();
		walkTask = taskManager.getTaskFor(WalkTask.class);
		eatTask = taskManager.getTaskFor(EatTask.class);
		running = true;
		return true;
	}

	@Override
	public synchronized void stop() {
		running = false;
		currentlyBreaking = null;
		nextTarget = null;
		ticksSinceBreak = 0;
	}

	@Override
	public synchronized void run() {
		if(currentlyBreaking != null) {
			ticksSinceBreak++;
			if(ticksSinceBreak > 200) {
				currentlyBreaking = null;
				nextTarget = null;
			}
			return;
		}
		ticksSinceBreak = 0;
		if(eatTask.isActive())
			return;
		if(ticksWait > 0) {
			ticksWait--;
			return;
		}
		World world = bot.getWorld();
		MainPlayerEntity player = bot.getPlayer();
		if(nextTarget != null && player.getDistanceTo(nextTarget) < 5) {
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
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player
				.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player
				.getZ() - 0.5)));
		checkTorches(ourLocation);
		if(BlockType.getById(
				world.getBlockIdAt(ourLocation.getX(), ourLocation.getY() + 1,
						ourLocation.getZ())).isSolid()) {
			breakBlock(new BlockLocation(ourLocation.getX(),
					ourLocation.getY() + 1, ourLocation.getZ()));
			return;
		} else if(BlockType.getById(world.getBlockIdAt(ourLocation)).isSolid()) {
			breakBlock(ourLocation);
			return;
		}
		BlockLocation newLocation = null;
		if(ourLocation.getY() < 11) {
			for(int offset = 1; offset < Math.max(11 - ourLocation.getY(), 3); offset++) {
				newLocation = ourLocation.offset(offset == 1 ? -1 : 0,
						offset == 1 ? 2 : offset - 1, 0);
				if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
					breakBlock(newLocation);
					return;
				}
			}
			int belowId = world.getBlockIdAt(newLocation.getX(),
					newLocation.getY() - 1, newLocation.getZ());
			if(belowId == 0 || belowId == 8 || belowId == 9 || belowId == 10
					|| belowId == 11)
				placeBlockAt(newLocation.offset(0, -1, 0));
			player.face(newLocation.getX(), newLocation.getY() + 1,
					newLocation.getZ());
			walkTask.setTarget(newLocation);
		} else if(ourLocation.getY() > 11) {
			for(int offset = 1; offset >= -1; offset--) {
				newLocation = new BlockLocation(ourLocation.getX() - 1,
						ourLocation.getY() + offset, ourLocation.getZ());
				if(BlockType.getById(world.getBlockIdAt(newLocation)).isSolid()) {
					breakBlock(newLocation);
					return;
				}
			}
			int belowId = world.getBlockIdAt(newLocation.getX(),
					newLocation.getY() - 1, newLocation.getZ());
			if(belowId == 0 || belowId == 8 || belowId == 9 || belowId == 10
					|| belowId == 11) {
				boolean success = placeBlockAt(newLocation.offset(0, -1, 0));
				if(!success)
					success = placeBlockAt(newLocation.offset(1, -1, 0));
				if(!success) {
					BlockLocation originalLocation = newLocation;
					BlockLocation locationOffset = newLocation.offset(0, -1, 0);
					while(!BlockType
							.getById(world.getBlockIdAt(locationOffset))
							.isSolid()) {
						newLocation = locationOffset;
						if(originalLocation.getY() - locationOffset.getY() > 5)
							return;
						locationOffset = locationOffset.offset(0, -1, 0);
					}
				} else {
					ticksWait = 4;
					return;
				}
			}
			player.face(newLocation.getX(), newLocation.getY() + 1,
					newLocation.getZ());
			walkTask.setTarget(newLocation);
		} else {
			if(ourLocation.getX() % 3 == 0) {
				if(xStart != ourLocation.getX()) {
					xStart = ourLocation.getX();
					zStart = ourLocation.getZ();
				}
				if(zStart != ourLocation.getZ()) {
					int zOffset = ourLocation.getZ() - zStart;
					if(Math.abs(zOffset) >= TUNNEL_LENGTH) {
						if(zOffset > 0 && zDirection > 0 || zOffset < 0
								&& zDirection < 0)
							zDirection *= -1;
					}
					for(int offset = 1; offset >= 0; offset--) {
						newLocation = new BlockLocation(ourLocation.getX(),
								ourLocation.getY() + offset, ourLocation.getZ()
										+ zDirection);
						if(BlockType.getById(world.getBlockIdAt(newLocation))
								.isSolid()) {
							breakBlock(newLocation);
							return;
						}
					}
				} else {
					if(BlockType
							.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY(),
											ourLocation.getZ() + 1)).isSolid()
							|| BlockType.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY() + 1,
											ourLocation.getZ() + 1)).isSolid()
							|| BlockType.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY(),
											ourLocation.getZ() + 2)).isSolid()
							|| BlockType.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY() + 1,
											ourLocation.getZ() + 2)).isSolid())
						zDirection = 1;
					else if(BlockType
							.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY(),
											ourLocation.getZ() - 1)).isSolid()
							|| BlockType.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY() + 1,
											ourLocation.getZ() - 1)).isSolid()
							|| BlockType.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY(),
											ourLocation.getZ() - 2)).isSolid()
							|| BlockType.getById(
									world.getBlockIdAt(ourLocation.getX(),
											ourLocation.getY() + 1,
											ourLocation.getZ() - 2)).isSolid())
						zDirection = -1;
					else {
						for(int offset = 1; offset >= 0; offset--) {
							newLocation = new BlockLocation(
									ourLocation.getX() - 1, ourLocation.getY()
											+ offset, ourLocation.getZ());
							if(BlockType.getById(
									world.getBlockIdAt(newLocation)).isSolid()) {
								breakBlock(newLocation);
								return;
							}
						}
						int belowId = world.getBlockIdAt(newLocation.getX(),
								newLocation.getY() - 1, newLocation.getZ());
						if(belowId == 0 || belowId == 8 || belowId == 9
								|| belowId == 10 || belowId == 11)
							placeBlockAt(new BlockLocation(newLocation.getX(),
									newLocation.getY() - 1, newLocation.getZ()));
						player.face(newLocation.getX(), newLocation.getY() + 1,
								newLocation.getZ());
						walkTask.setTarget(newLocation);
						return;
					}
					for(int offset = 1; offset >= 0; offset--) {
						newLocation = new BlockLocation(ourLocation.getX(),
								ourLocation.getY() + offset, ourLocation.getZ()
										+ zDirection);
						if(BlockType.getById(world.getBlockIdAt(newLocation))
								.isSolid()) {
							breakBlock(newLocation);
							return;
						}
					}
				}
			} else {
				for(int offset = 1; offset >= 0; offset--) {
					newLocation = new BlockLocation(ourLocation.getX() - 1,
							ourLocation.getY() + offset, ourLocation.getZ());
					if(BlockType.getById(world.getBlockIdAt(newLocation))
							.isSolid()) {
						breakBlock(newLocation);
						return;
					}
				}
			}
			int belowId = world.getBlockIdAt(newLocation.getX(),
					newLocation.getY() - 1, newLocation.getZ());
			if(belowId == 0 || belowId == 8 || belowId == 9 || belowId == 10
					|| belowId == 11)
				placeBlockAt(new BlockLocation(newLocation.getX(),
						newLocation.getY() - 1, newLocation.getZ()));
			player.face(newLocation.getX(), newLocation.getY() + 1,
					newLocation.getZ());
			walkTask.setTarget(newLocation);
		}
	}

	@Override
	public synchronized boolean isActive() {
		return running;
	}

	@EventHandler
	public synchronized void onBlockChange(BlockChangeEvent event) {
		BlockLocation location = event.getLocation();
		Block newBlock = event.getNewBlock();
		if((event.getOldBlock() == null && newBlock == null)
				|| (event.getOldBlock() != null && newBlock != null && event
						.getOldBlock().getId() == newBlock.getId()))
			return;
		if(newBlock == null || newBlock.getId() == 0) {
			if(currentlyBreaking != null && currentlyBreaking.equals(location)) {
				currentlyBreaking = null;
				System.out.println("No longer breaking.");
			}
		}
	}

	private void breakBlock(BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null)
			return;
		player.face(x, y, z);
		int idAbove = world.getBlockIdAt(x, y + 1, z);
		if(idAbove == 12 || idAbove == 13) {
			ticksWait = 30;
			nextTarget = location;
		} else if(nextTarget == null && previous != null)
			nextTarget = checkSurrounding(previous);
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		switchToBestTool(world.getBlockIdAt(location));
		connectionHandler.sendPacket(new Packet12PlayerLook((float) player
				.getYaw(), (float) player.getPitch(), true));
		connectionHandler.sendPacket(new Packet18Animation(player.getId(),
				Animation.SWING_ARM));
		connectionHandler.sendPacket(new Packet14BlockDig(0, x, y, z, 0));
		connectionHandler.sendPacket(new Packet14BlockDig(2, x, y, z, 0));
		currentlyBreaking = location;
		previous = location;
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

	private void switchToBestTool(int id) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		PlayerInventory inventory = player.getInventory();
		int toolType = 0;
		if(id > 0 && id < 256)
			if(MINEABLE[id])
				toolType = 1;
			else if(DIGGABLE[id])
				toolType = 2;
		ItemStack bestTool = null;
		int bestToolSlot = -1, bestToolValue = -1;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(toolType == 0 && i > 8)
				break;
			if(toolType == 0
					&& (item == null || (PICKAXES[item.getId()] == 0 && SHOVELS[item
							.getId()] == 0))) {
				bestTool = item;
				bestToolSlot = i;
				break;
			} else if(toolType == 0)
				continue;
			if(item != null) {
				int toolValue = toolType == 1 ? PICKAXES[item.getId()]
						: SHOVELS[item.getId()];
				if(bestTool != null ? toolValue > bestToolValue : toolValue > 0) {
					bestTool = item;
					bestToolSlot = i;
					bestToolValue = toolValue;
				}
			}
		}
		if(toolType == 0) {
			if(bestToolSlot != -1
					&& inventory.getCurrentHeldSlot() != bestToolSlot)
				inventory.setCurrentHeldSlot(bestToolSlot);
		} else if(bestTool != null) {
			if(inventory.getCurrentHeldSlot() != bestToolSlot) {
				if(bestToolSlot > 8) {
					int hotbarSpace = 9;
					for(int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
						if(inventory.getItemAt(hotbarIndex) == null) {
							hotbarSpace = hotbarIndex;
							break;
						} else if(PICKAXES[hotbarIndex] == 0
								&& SHOVELS[hotbarIndex] == 0
								&& hotbarIndex < hotbarSpace) {
							hotbarSpace = hotbarIndex;
						}
					}
					if(hotbarSpace == 9)
						return;
					inventory.selectItemAt(bestToolSlot);
					inventory.selectItemAt(hotbarSpace);
					if(inventory.getSelectedItem() != null)
						inventory.selectItemAt(bestToolSlot);
					inventory.close();
					bestToolSlot = hotbarSpace;
				}
				inventory.setCurrentHeldSlot(bestToolSlot);
			}
		}
	}

	private boolean placeBlockAt(BlockLocation location) {
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
		if(inventory.getCurrentHeldSlot() != slot) {
			if(slot > 8) {
				int hotbarSpace = 9;
				for(int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
					if(inventory.getItemAt(hotbarIndex) == null) {
						hotbarSpace = hotbarIndex;
						break;
					} else if(PICKAXES[hotbarIndex] == 0
							&& SHOVELS[hotbarIndex] == 0
							&& hotbarIndex < hotbarSpace) {
						hotbarSpace = hotbarIndex;
					}
				}
				if(hotbarSpace == 9)
					return false;
				inventory.selectItemAt(slot);
				inventory.selectItemAt(hotbarSpace);
				if(inventory.getSelectedItem() != null)
					inventory.selectItemAt(slot);
				inventory.close();
				slot = hotbarSpace;
			}
			inventory.setCurrentHeldSlot(slot);
		}
		return placeAt(location);
	}

	private boolean placeAt(BlockLocation location) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		PlayerInventory inventory = player.getInventory();
		int originalX = location.getX(), originalY = location.getY(), originalZ = location
				.getZ();
		int face = getPlacementBlockFaceAt(location);
		location = getOffsetBlock(location, face);
		if(location == null)
			return false;
		int x = location.getX(), y = location.getY(), z = location.getZ();
		player.face(x + ((originalX - x) / 2.0D), y + ((originalY - y) / 2.0D),
				z + ((originalZ - z) / 2.0D));
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		connectionHandler.sendPacket(new Packet12PlayerLook((float) player
				.getYaw(), (float) player.getPitch(), true));
		connectionHandler.sendPacket(new Packet18Animation(player.getId(),
				Animation.SWING_ARM));
		Packet15Place placePacket = new Packet15Place();
		placePacket.xPosition = x;
		placePacket.yPosition = y;
		placePacket.zPosition = z;
		placePacket.direction = face;
		placePacket.itemStack = inventory.getCurrentHeldItem();
		connectionHandler.sendPacket(placePacket);
		ticksWait = 4;
		return true;
	}

	private int getPlacementBlockFaceAt(BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		World world = bot.getWorld();
		if(!UNPLACEABLE[world.getBlockIdAt(x + 1, y, z)]) {
			return 4;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x, y, z - 1)]) {
			return 3;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x, y, z + 1)]) {
			return 2;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x, y - 1, z)]) {
			return 1;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x - 1, y, z)]) {
			return 5;
		} else
			return -1;
	}

	private BlockLocation getOffsetBlock(BlockLocation location, int face) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		switch(face) {
		case 0:
			y++;
			break;
		case 1:
			y--;
			break;
		case 2:
			z++;
			break;
		case 3:
			z--;
			break;
		case 4:
			x++;
			break;
		case 5:
			x--;
			break;
		default:
			return null;
		}
		return new BlockLocation(x, y, z);
	}

	private void checkTorches(BlockLocation ourLocation) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		if(lastLocation == null
				|| ((ourLocation.getX() == lastLocation.getX() || Math
						.abs(ourLocation.getX() % 6) != 1) && (ourLocation
						.getZ() == lastLocation.getZ() || Math.abs(ourLocation
						.getZ() % 6) != 1))) {
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
		if(inventory.getCurrentHeldSlot() != slot) {
			if(slot > 8) {
				int hotbarSpace = 9;
				for(int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
					if(inventory.getItemAt(hotbarIndex) == null) {
						hotbarSpace = hotbarIndex;
						break;
					} else if(PICKAXES[hotbarIndex] == 0
							&& SHOVELS[hotbarIndex] == 0
							&& hotbarIndex < hotbarSpace) {
						hotbarSpace = hotbarIndex;
					}
				}
				if(hotbarSpace == 9)
					return;
				inventory.selectItemAt(slot);
				inventory.selectItemAt(hotbarSpace);
				if(inventory.getSelectedItem() != null)
					inventory.selectItemAt(slot);
				inventory.close();
				slot = hotbarSpace;
			}
			inventory.setCurrentHeldSlot(slot);
		}

		placeAt(new BlockLocation(ourLocation.getX(), ourLocation.getY() + 1,
				ourLocation.getZ()));
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
