package org.darkstorm.darkbot.minecraftbot.ai;

import java.util.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.world.BlockChangeEvent;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class FarmingTask implements Task, EventListener {
	private static final boolean[] UNPLACEABLE = new boolean[256];
	private static final int[] HOES;

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

		HOES = new int[] { 290, 291, 292, 293, 294 };
	}

	private final MinecraftBot bot;

	private boolean running = false;

	private BlockLocation currentlyBreaking;
	private int ticksSinceBreak, ticksWait, itemCheckWait;

	private BlockLocation currentChest;
	private List<BlockLocation> fullChests = new ArrayList<BlockLocation>();

	public FarmingTask(final MinecraftBot bot) {
		this.bot = bot;
		bot.getEventManager().registerListener(this);
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return running;
	}

	@Override
	public synchronized boolean start(String... options) {
		running = true;
		return true;
	}

	@Override
	public synchronized void stop() {
		running = false;
	}

	@Override
	public synchronized void run() {
		if(currentlyBreaking != null) {
			ticksSinceBreak++;
			if(ticksSinceBreak > 200)
				currentlyBreaking = null;
			return;
		}
		ticksSinceBreak = 0;
		TaskManager taskManager = bot.getTaskManager();
		WalkTask walkTask = taskManager.getTaskFor(WalkTask.class);
		EatTask eatTask = taskManager.getTaskFor(EatTask.class);
		if(eatTask.isActive())
			return;
		if(ticksWait > 0) {
			ticksWait--;
			return;
		}
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return;
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player
				.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player
				.getZ() - 0.5)));
		PlayerInventory inventory = player.getInventory();
		if(!inventory.contains(0)) {
			if(bot.getCurrentWindow() instanceof ChestInventory) {
				ChestInventory chest = (ChestInventory) bot.getCurrentWindow();
				int freeSpace = -1;
				for(int i = 0; i < 27; i++)
					if(chest.getItemAt(i) == null)
						freeSpace = i;
				if(freeSpace == -1) {
					if(currentChest != null) {
						fullChests.add(currentChest);
						currentChest = null;
					}
					chest.close();
					ticksWait = 4;
					return;
				}
				for(int i = 27; i < chest.getSize(); i++) {

				}
			} else {
				BlockLocation[] chests = getBlocks(54, 32);
				chestLoop: for(BlockLocation chest : chests) {
					if(!fullChests.contains(chest)) {
						BlockLocation[] surrounding = new BlockLocation[] {
								chest.offset(0, 1, 0), chest.offset(-1, 0, 0),
								chest.offset(1, 0, 0), chest.offset(0, 0, -1),
								chest.offset(0, 0, 1) };
						BlockLocation closestWalk = null;
						int closestDistance = Integer.MAX_VALUE;
						int face = 0;
						for(BlockLocation walk : surrounding) {
							if(BlockType.getById(world.getBlockIdAt(walk))
									.isSolid())
								continue;
							int distance = ourLocation
									.getDistanceToSquared(walk);
							if(distance < closestDistance) {
								closestWalk = walk;
								closestDistance = distance;
								if(walk.getY() > chest.getY())
									face = 1;
								else if(walk.getX() > chest.getX())
									face = 5;
								else if(walk.getX() < chest.getX())
									face = 4;
								else if(walk.getZ() > chest.getZ())
									face = 3;
								else if(walk.getZ() < chest.getZ())
									face = 2;
								else
									face = 0;
							}
						}
						if(closestWalk == null)
							continue chestLoop;
						BlockLocation originalWalk = closestWalk;
						BlockLocation closestWalkOffset = closestWalk.offset(0,
								-1, 0);
						while(!BlockType.getById(
								world.getBlockIdAt(closestWalkOffset))
								.isSolid()) {
							closestWalk = closestWalkOffset;
							if(originalWalk.getY() - closestWalkOffset.getY() > 5)
								continue chestLoop;
							closestWalkOffset = closestWalkOffset.offset(0, -1,
									0);
						}

						if(!ourLocation.equals(closestWalk)) {
							walkTask.setTarget(closestWalk);
							return;
						}

						placeAt(originalWalk, face);
						ticksWait = 15;
					}
				}
			}
		}

		BlockLocation closest = getClosestFarmable();
		if(closest == null) {
			if(itemCheckWait > 0) {
				itemCheckWait--;
				return;
			}
			if(!inventory.contains(0)) {
				itemCheckWait = 10;
				return;
			}
			ItemEntity item = getClosestGroundItem(372, 295, 296, 338, 361,
					362, 86, 360);
			if(item != null) {
				System.out.println("Item: " + item.getItem() + " Location: "
						+ item.getLocation());
				walkTask.setTarget(new BlockLocation(item.getLocation()));
			} else
				itemCheckWait = 10;
			return;
		}
		itemCheckWait = 0;
		System.out.println("Farming at " + closest + "!");
		int id = world.getBlockIdAt(closest);
		if(id == 115 || id == 59 || id == 83 || id == 86 || id == 103) {
			System.out.println("Target: " + id + "-"
					+ world.getBlockMetadataAt(closest));
			BlockLocation walkTo = closest;
			if(id == 83)
				walkTo = closest.offset(0, -1, 0);
			else if(id == 86 || id == 103) {
				BlockLocation[] surrounding = new BlockLocation[] {
						closest.offset(0, 1, 0), closest.offset(-1, 0, 0),
						closest.offset(1, 0, 0), closest.offset(0, 0, -1),
						closest.offset(0, 0, 1) };
				BlockLocation closestWalk = null;
				int closestDistance = Integer.MAX_VALUE;
				for(BlockLocation walk : surrounding) {
					if(BlockType.getById(world.getBlockIdAt(walk)).isSolid())
						continue;
					int distance = ourLocation.getDistanceToSquared(walk);
					if(distance < closestDistance) {
						closestWalk = walk;
						closestDistance = distance;
					}
				}
				if(closestWalk == null)
					return;
				BlockLocation originalWalk = closestWalk;
				BlockLocation closestWalkOffset = closestWalk.offset(0, -1, 0);
				while(!BlockType.getById(world.getBlockIdAt(closestWalkOffset))
						.isSolid()) {
					closestWalk = closestWalkOffset;
					if(originalWalk.getY() - closestWalkOffset.getY() > 5)
						return;
					closestWalkOffset = closestWalkOffset.offset(0, -1, 0);
				}
				walkTo = closestWalk;
			}
			if(!ourLocation.equals(walkTo)) {
				walkTask.setTarget(walkTo);
				return;
			}
			breakBlock(closest);
		} else if(id == 88 || id == 60 || id == 3 || id == 104 || id == 105) {
			if(id == 104 || id == 105) {
				BlockLocation[] locations = new BlockLocation[] {
						closest.offset(-1, -1, 0), closest.offset(1, -1, 0),
						closest.offset(0, -1, -1), closest.offset(0, -1, -1) };
				for(BlockLocation dirtLocation : locations)
					if(world.getBlockIdAt(dirtLocation) == 3)
						closest = dirtLocation;
			}
			int[] tools;
			if(id == 88)
				tools = new int[] { 372 };
			else if(id == 60)
				tools = new int[] { 295 };
			else if(((id == 3 && inventory.contains(295)) || id == 104 || id == 105)
					&& inventory.contains(HOES))
				tools = HOES;
			// else if(inventory.contains(338) && (id == 3 || id == 12))
			// tools = new int[] { 338 };
			else
				return;
			if(!switchTo(tools))
				return;
			BlockLocation offset = closest.offset(0, 1, 0);
			if(!ourLocation.equals(offset)) {
				walkTask.setTarget(offset);
				return;
			}
			placeAt(offset);
			ticksWait = 5;
		}
	}

	private ItemEntity getClosestGroundItem(int... ids) {
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return null;
		Entity[] entities = world.getEntities();
		ItemEntity closest = null;
		int closestDistance = Integer.MAX_VALUE;
		for(Entity entity : entities) {
			if(entity instanceof ItemEntity) {
				ItemEntity item = (ItemEntity) entity;
				if(item.getItem() == null)
					continue;
				int itemId = item.getItem().getId();
				for(int id : ids) {
					if(itemId == id) {
						int distance = player.getDistanceToSquared(item);
						if(distance < closestDistance) {
							int blockId = world.getBlockIdAt(new BlockLocation(
									item.getLocation()));
							if(!BlockType.getById(blockId).isSolid()) {
								closest = item;
								closestDistance = distance;
							}
						}
					}
				}
			}
		}
		return closest;
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
			}
		}
	}

	private BlockLocation getClosestFarmable() {
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return null;
		PlayerInventory inventory = player.getInventory();
		boolean hasNetherwarts = inventory.contains(372), hasSeeds = inventory
				.contains(295), hasHoe = inventory.contains(HOES);
		// boolean hasReeds = inventory.contains(338);
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player
				.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player
				.getZ() - 0.5)));
		int radius = 32;
		BlockLocation closestLocation = null;
		int closestDistance = Integer.MAX_VALUE, farmType = 0;
		for(int x = -radius; x < radius; x++) {
			for(int y = -radius / 2; y < radius / 2; y++) {
				for(int z = -radius; z < radius; z++) {
					BlockLocation location = new BlockLocation(
							ourLocation.getX() + x, ourLocation.getY() + y,
							ourLocation.getZ() + z);
					int distance = ourLocation.getDistanceToSquared(location);
					if(distance < closestDistance) {
						// System.out.println("[" + x + "," + y + "," + z + "] "
						// + distance + " -> " + closestDistance);
						int id = world.getBlockIdAt(location);
						int idAbove = world.getBlockIdAt(location.offset(0, 1,
								0));
						int idBelow = world.getBlockIdAt(location.offset(0, -1,
								0));
						int metadata = world.getBlockMetadataAt(location);

						boolean pumpkinWatermelonDirt = false;
						boolean plantSeeds = true;
						if(farmType <= 3 && (id == 104 || id == 105) && hasHoe) {
							BlockLocation[] locations = new BlockLocation[] {
									location.offset(-1, -1, 0),
									location.offset(1, -1, 0),
									location.offset(0, -1, -1),
									location.offset(0, -1, 1) };
							for(BlockLocation dirtLocation : locations)
								if(world.getBlockIdAt(dirtLocation) == 3
										&& world.getBlockIdAt(dirtLocation
												.offset(0, 1, 0)) == 0)
									pumpkinWatermelonDirt = true;
						}
						if(farmType <= 1
								&& (id == 3 && idAbove == 0 && hasHoe && hasSeeds)) {
							BlockLocation[] locations = new BlockLocation[] {
									location.offset(-1, 0, 0),
									location.offset(1, 0, 0),
									location.offset(0, 0, -1),
									location.offset(0, 0, 1) };
							for(BlockLocation adjacent : locations) {
								int adjacentId = world.getBlockIdAt(adjacent);
								if(adjacentId == 104 || adjacentId == 105)
									plantSeeds = false;
							}
						}
						if(farmType <= 3
								&& (pumpkinWatermelonDirt || id == 103
										|| id == 86
										|| (id == 115 && metadata > 2)
										|| (id == 59 && metadata > 6) || (id == 83
										&& idBelow == 83 && idAbove == 83))) {
							farmType = 3;
						} else if(farmType <= 2
								&& ((id == 88 && idAbove == 0 && hasNetherwarts) || (id == 60
										&& idAbove == 0 && hasSeeds)))
							farmType = 2;
						// else if(farmType < 2
						// && ((id == 3 || id == 12) && idAbove == 0 &&
						// hasReeds))
						// farmType = 2;
						else if(farmType <= 1
								&& (id == 3 && idAbove == 0 && hasHoe
										&& hasSeeds && plantSeeds))
							farmType = 1;
						else
							continue;
						System.out.println(distance + ", " + closestDistance
								+ " " + location + " -> " + ourLocation);
						closestLocation = location;
						closestDistance = distance;
					}
				}
			}
		}
		return closestLocation;
	}

	@SuppressWarnings("unused")
	private BlockLocation getClosestBlock(int id, int radius) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return null;
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player
				.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player
				.getZ() - 0.5)));
		BlockLocation closest = null;
		int closestDistance = Integer.MAX_VALUE;
		for(BlockLocation location : getBlocks(id, radius)) {
			int distance = ourLocation.getDistanceToSquared(location);
			if(distance < closestDistance) {
				closest = location;
				closestDistance = distance;
			}
		}
		return closest;
	}

	private BlockLocation[] getBlocks(int id, int radius) {
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return new BlockLocation[0];
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player
				.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player
				.getZ() - 0.5)));
		List<BlockLocation> blocks = new ArrayList<BlockLocation>();
		for(int x = -radius; x < radius; x++) {
			for(int y = -radius; y < radius; y++) {
				for(int z = -radius; z < radius; z++) {
					BlockLocation location = new BlockLocation(
							ourLocation.getX() + x, ourLocation.getY() + y,
							ourLocation.getZ() + z);
					if(world.getBlockIdAt(location) == id)
						blocks.add(location);
				}
			}
		}
		return blocks.toArray(new BlockLocation[blocks.size()]);
	}

	private boolean switchTo(int... toolIds) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		PlayerInventory inventory = player.getInventory();
		int slot = -1;
		label: for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item == null)
				continue;
			int id = item.getId();
			for(int toolId : toolIds) {
				if(id == toolId) {
					slot = i;
					break label;
				}
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
					} else if(hotbarIndex < hotbarSpace)
						hotbarSpace = hotbarIndex;
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
		return true;
	}

	private void breakBlock(BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		player.face(x, y, z);
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		connectionHandler.sendPacket(new Packet12PlayerLook((float) player
				.getYaw(), (float) player.getPitch(), true));
		connectionHandler.sendPacket(new Packet18Animation(player.getId(),
				Animation.SWING_ARM));
		connectionHandler.sendPacket(new Packet14BlockDig(0, x, y, z, 0));
		connectionHandler.sendPacket(new Packet14BlockDig(2, x, y, z, 0));
		currentlyBreaking = location;
	}

	private void placeAt(BlockLocation location) {
		placeAt(location, getPlacementBlockFaceAt(location));
	}

	private void placeAt(BlockLocation location, int face) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		PlayerInventory inventory = player.getInventory();
		int originalX = location.getX(), originalY = location.getY(), originalZ = location
				.getZ();
		location = getOffsetBlock(location, face);
		if(location == null)
			return;
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
	}

	private int getPlacementBlockFaceAt(BlockLocation location) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		World world = bot.getWorld();
		if(!UNPLACEABLE[world.getBlockIdAt(x, y - 1, z)]) {
			return 1;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x, y + 1, z)]) {
			return 0;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x + 1, y, z)]) {
			return 4;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x, y, z - 1)]) {
			return 3;
		} else if(!UNPLACEABLE[world.getBlockIdAt(x, y, z + 1)]) {
			return 2;
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
		return "Farm";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
