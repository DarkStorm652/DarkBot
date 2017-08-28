package org.darkstorm.minecraft.darkbot.ai;

import java.util.*;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.*;
import org.darkstorm.minecraft.darkbot.world.entity.*;
import org.darkstorm.minecraft.darkbot.world.item.*;

public class FarmingTask extends AbstractTask implements EventListener {
	public enum StorageAction {
		STORE,
		SELL
	}

	private static final boolean[] UNPLACEABLE = new boolean[256];
	private static final int[] HOES;
	private static final int[] FARMED_ITEMS;

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
		FARMED_ITEMS = new int[] { 372, 295, 296, 338, 361, 362, 86, 360 };
	}

	private boolean running = false;
	private int ticksWait, itemCheckWait;
	private BlockLocation currentChest;
	private List<BlockLocation> fullChests = new ArrayList<BlockLocation>();
	private BlockArea region;
	private StorageAction storageAction = StorageAction.STORE;
	private boolean selling;

	public FarmingTask(final MinecraftBot bot) {
		super(bot);
		bot.getEventBus().register(this);
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return running;
	}

	@Override
	public synchronized boolean start(String... options) {
		if(options.length > 0) {
			BlockLocation endpoint1 = new BlockLocation(Integer.parseInt(options[0]), Integer.parseInt(options[1]), Integer.parseInt(options[2]));
			BlockLocation endpoint2 = new BlockLocation(Integer.parseInt(options[3]), Integer.parseInt(options[4]), Integer.parseInt(options[5]));
			region = new BlockArea(endpoint1, endpoint2);
		} else
			region = null;
		running = true;
		return true;
	}

	@Override
	public synchronized void stop() {
		running = false;
	}

	@Override
	public synchronized void run() {
		TaskManager taskManager = bot.getTaskManager();
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
		BlockLocation ourLocation = new BlockLocation(player.getLocation());
		PlayerInventory inventory = player.getInventory();
		boolean store = !inventory.contains(0);
		if(!store && storageAction.equals(StorageAction.SELL) && selling)
			for(int id : FARMED_ITEMS)
				if(inventory.getCount(id) >= 64)
					store = true;
		if(store) {
			System.out.println("Inventory is full!!!");
			if(storageAction.equals(StorageAction.STORE)) {
				if(player.getWindow() instanceof GenericInventory) {
					System.out.println("Chest is open!!!");
					GenericInventory chest = (GenericInventory) player.getWindow();
					int freeSpace = -1;
					for(int i = 0; i < chest.getSize(); i++)
						if(chest.getItemAt(i) == null)
							freeSpace = i;
					if(freeSpace == -1) {
						if(currentChest != null) {
							fullChests.add(currentChest);
							placeBlockAt(currentChest.offset(0, 1, 0));
							currentChest = null;
						}
						chest.close();
						System.out.println("Closed chest, no spaces!!!");
						ticksWait = 16;
						return;
					}
					for(int i = 0; i < 36; i++) {
						ItemStack item = chest.getItemAt(chest.getSize() + i);
						if(item == null)
							continue;
						boolean found = false;
						for(int id : FARMED_ITEMS)
							if(id == item.getId())
								found = true;
						if(!found)
							continue;
						chest.selectItemAt(chest.getSize() + i);
						int index = -1;
						for(int j = 0; j < chest.getSize(); j++) {
							if(chest.getItemAt(j) == null) {
								index = j;
								break;
							}
						}
						if(index == -1)
							continue;
						chest.selectItemAt(index);
					}
					freeSpace = -1;
					for(int i = 0; i < chest.getSize(); i++)
						if(chest.getItemAt(i) == null)
							freeSpace = i;
					if(freeSpace == -1 && currentChest != null) {
						fullChests.add(currentChest);
						placeBlockAt(currentChest.offset(0, 1, 0));
						currentChest = null;
					}
					chest.close();
					currentChest = null;
					System.out.println("Closed chest!!!");
					ticksWait = 16;
					return;
				} else {
					BlockLocation[] chests = getBlocks(54, 32);
					chestLoop: for(BlockLocation chest : chests) {
						if(!fullChests.contains(chest) && !isChestCovered(chest)) {
							BlockLocation[] surrounding = new BlockLocation[] { chest.offset(0, 1, 0), chest.offset(-1, 0, 0), chest.offset(1, 0, 0), chest.offset(0, 0, -1), chest.offset(0, 0, 1) };
							BlockLocation closestWalk = null;
							long closestDistance = Long.MAX_VALUE;
							int face = 0;
							for(BlockLocation walk : surrounding) {
								if(BlockType.getById(world.getBlockIdAt(walk)).isSolid() || (BlockType.getById(world.getBlockIdAt(walk.offset(0, 1, 0))).isSolid() && BlockType.getById(world.getBlockIdAt(walk.offset(0, -1, 0))).isSolid()))
									continue;
								long distance = ourLocation.getDistanceToSquared(walk);
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
							BlockLocation closestWalkOffset = closestWalk.offset(0, -1, 0);
							while(!BlockType.getById(world.getBlockIdAt(closestWalkOffset)).isSolid()) {
								closestWalk = closestWalkOffset;
								if(originalWalk.getY() - closestWalkOffset.getY() > 5)
									continue chestLoop;
								closestWalkOffset = closestWalkOffset.offset(0, -1, 0);
							}

							if(!ourLocation.equals(closestWalk)) {
								System.out.println("Walking to chest!!!");
								setActivity(new WalkActivity(bot, closestWalk));
								return;
							}

							System.out.println("Opening chest!!!");
							player.placeBlock(originalWalk, face);
							currentChest = chest;
							ticksWait = 80;
							return;
						}
					}
				}
			} else if(storageAction.equals(StorageAction.SELL)) {
				if(region != null ? region.contains(ourLocation) : getClosestFarmable(32) != null) {
					bot.sendChat("/spawn");
					ticksWait = 200;
					return;
				}
				selling = true;
				BlockLocation[] signs = getBlocks(68, 32);
				signLoop: for(BlockLocation sign : signs) {
					TileEntity tile = world.getTileEntityAt(sign);
					if(tile == null || !(tile instanceof SignTileEntity))
						continue;
					SignTileEntity signTile = (SignTileEntity) tile;
					String[] text = signTile.getText();
					boolean found = false;
					if(text[0].contains("[Sell]"))
						for(int id : FARMED_ITEMS)
							if(text[2].equals(Integer.toString(id)) && inventory.getCount(id) >= 64)
								found = true;
					if(!found)
						continue;

					if(player.getDistanceTo(sign) > 3) {
						BlockLocation closestWalk = sign;
						BlockLocation originalWalk = closestWalk;
						BlockLocation closestWalkOffset = closestWalk.offset(0, -1, 0);
						while(!BlockType.getById(world.getBlockIdAt(closestWalkOffset)).isSolid()) {
							closestWalk = closestWalkOffset;
							if(originalWalk.getY() - closestWalkOffset.getY() > 5)
								continue signLoop;
							closestWalkOffset = closestWalkOffset.offset(0, -1, 0);
						}
						player.walkTo(closestWalk);
						System.out.println("Walking to sign @ " + sign);
						return;
					}
					BlockLocation[] surrounding = new BlockLocation[] { sign.offset(0, 1, 0), sign.offset(-1, 0, 0), sign.offset(1, 0, 0), sign.offset(0, 0, -1), sign.offset(0, 0, 1), sign.offset(0, -1, 0) };
					BlockLocation closestWalk = null;
					long closestDistance = Long.MAX_VALUE;
					int face = 0;
					for(BlockLocation walk : surrounding) {
						if(BlockType.getById(world.getBlockIdAt(walk)).isSolid() || (BlockType.getById(world.getBlockIdAt(walk.offset(0, 1, 0))).isSolid() && BlockType.getById(world.getBlockIdAt(walk.offset(0, -1, 0))).isSolid()))
							continue;
						long distance = ourLocation.getDistanceToSquared(walk);
						if(distance < closestDistance) {
							closestWalk = walk;
							closestDistance = distance;
							if(walk.getY() > sign.getY())
								face = 1;
							else if(walk.getX() > sign.getX())
								face = 5;
							else if(walk.getX() < sign.getX())
								face = 4;
							else if(walk.getZ() > sign.getZ())
								face = 3;
							else if(walk.getZ() < sign.getZ())
								face = 2;
							else
								face = 0;
						}
					}
					if(closestWalk == null)
						continue;
					player.placeBlock(getOffsetBlock(sign, face), face);
					ticksWait = 4;
					return;
				}
				return;
			}
		}

		BlockLocation closest = getClosestFarmable(32);
		if(region != null ? !region.contains(ourLocation) : closest == null) {
			bot.sendChat("/home");
			ticksWait = 200;
			return;
		}
		selling = false;

		if(closest == null) {
			if(itemCheckWait > 0) {
				itemCheckWait--;
				return;
			}
			if(!inventory.contains(0)) {
				itemCheckWait = 10;
				return;
			}
			ItemEntity item = getClosestGroundItem(FARMED_ITEMS);
			if(item != null) {
				System.out.println("Item: " + item.getItem() + " Location: " + item.getLocation());
				setActivity(new WalkActivity(bot, new BlockLocation(item.getLocation())));
			} else
				itemCheckWait = 10;
			return;
		}
		itemCheckWait = 0;
		System.out.println("Farming at " + closest + "!");
		int id = world.getBlockIdAt(closest);
		if(id == 115 || id == 59 || id == 83 || id == 86 || id == 103) {
			System.out.println("Target: " + id + "-" + world.getBlockMetadataAt(closest));
			BlockLocation walkTo = closest;
			if(id == 83)
				walkTo = closest.offset(0, -1, 0);
			else if(id == 86 || id == 103) {
				BlockLocation[] surrounding = new BlockLocation[] { closest.offset(0, 1, 0), closest.offset(-1, 0, 0), closest.offset(1, 0, 0), closest.offset(0, 0, -1), closest.offset(0, 0, 1) };
				BlockLocation closestWalk = null;
				long closestDistance = Long.MAX_VALUE;
				for(BlockLocation walk : surrounding) {
					if(BlockType.getById(world.getBlockIdAt(walk)).isSolid())
						continue;
					long distance = ourLocation.getDistanceToSquared(walk);
					if(distance < closestDistance) {
						closestWalk = walk;
						closestDistance = distance;
					}
				}
				if(closestWalk == null)
					return;
				BlockLocation originalWalk = closestWalk;
				BlockLocation closestWalkOffset = closestWalk.offset(0, -1, 0);
				while(!BlockType.getById(world.getBlockIdAt(closestWalkOffset)).isSolid()) {
					closestWalk = closestWalkOffset;
					if(originalWalk.getY() - closestWalkOffset.getY() > 5)
						return;
					closestWalkOffset = closestWalkOffset.offset(0, -1, 0);
				}
				walkTo = closestWalk;
			}
			if(!ourLocation.equals(walkTo)) {
				setActivity(new WalkActivity(bot, walkTo));
				return;
			}
			player.breakBlock(closest);
		} else if(id == 88 || id == 60 || id == 3 || id == 104 || id == 105) {
			if(id == 104 || id == 105) {
				BlockLocation[] locations = new BlockLocation[] { closest.offset(-1, -1, 0), closest.offset(1, -1, 0), closest.offset(0, -1, -1), closest.offset(0, -1, -1) };
				for(BlockLocation dirtLocation : locations)
					if(world.getBlockIdAt(dirtLocation) == 3)
						closest = dirtLocation;
			}
			int[] tools;
			if(id == 88)
				tools = new int[] { 372 };
			else if(id == 60)
				tools = new int[] { 295 };
			else if(((id == 3 && inventory.contains(295)) || id == 104 || id == 105) && inventory.contains(HOES))
				tools = HOES;
			// else if(inventory.contains(338) && (id == 3 || id == 12))
			// tools = new int[] { 338 };
			else
				return;
			if(!switchTo(tools))
				return;
			BlockLocation offset = closest.offset(0, 1, 0);
			if(!ourLocation.equals(offset)) {
				setActivity(new WalkActivity(bot, offset));
				return;
			}
			player.placeBlock(offset);
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
							int blockId = world.getBlockIdAt(new BlockLocation(item.getLocation()));
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

	private boolean isChestCovered(BlockLocation chest) {
		if(checkChest(chest))
			return true;
		BlockLocation[] surrounding = new BlockLocation[] { chest.offset(-1, 0, 0), chest.offset(1, 0, 0), chest.offset(0, 0, -1), chest.offset(0, 0, 1) };
		for(BlockLocation otherChest : surrounding)
			if(bot.getWorld().getBlockIdAt(otherChest) == 54 && checkChest(otherChest))
				return true;
		return false;
	}

	private boolean checkChest(BlockLocation chest) {
		int idAbove = bot.getWorld().getBlockIdAt(chest.offset(0, 1, 0));
		if(!BlockType.getById(idAbove).isSolid())
			return false;
		BlockType[] noncovers = new BlockType[] { BlockType.CHEST, BlockType.ENDER_CHEST, BlockType.STEP, BlockType.BED_BLOCK, BlockType.ANVIL, BlockType.BREWING_STAND, BlockType.WOOD_STEP, BlockType.WOOD_STAIRS, BlockType.BRICK_STAIRS, BlockType.COBBLESTONE_STAIRS, BlockType.NETHER_BRICK_STAIRS, BlockType.SANDSTONE_STAIRS, BlockType.SMOOTH_STAIRS };
		for(BlockType type : noncovers)
			if(idAbove == type.getId())
				return false;
		return true;
	}

	@Override
	public synchronized boolean isActive() {
		return running;
	}

	private BlockLocation getClosestFarmable(int radius) {
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return null;
		PlayerInventory inventory = player.getInventory();
		boolean hasNetherwarts = inventory.contains(372), hasSeeds = inventory.contains(295), hasHoe = inventory.contains(HOES);
		// boolean hasReeds = inventory.contains(338);
		BlockLocation ourLocation = new BlockLocation(player.getLocation());
		List<BlockLocation> closest = new ArrayList<>();
		long closestDistance = Long.MAX_VALUE;
		int actualFarmType = 0;
		for(int x = region != null ? region.getX() - ourLocation.getX() : -radius; x < (region != null ? region.getX() + region.getWidth() - ourLocation.getX() : radius); x++) {
			for(int y = region != null ? region.getY() - ourLocation.getY() : -radius / 2; y < (region != null ? region.getY() + region.getHeight() - ourLocation.getY() : radius / 2); y++) {
				for(int z = region != null ? region.getZ() - ourLocation.getZ() : -radius; z < (region != null ? region.getZ() + region.getLength() - ourLocation.getZ() : radius); z++) {
					BlockLocation location = new BlockLocation(ourLocation.getX() + x, ourLocation.getY() + y, ourLocation.getZ() + z);
					long distance = ourLocation.getDistanceToSquared(location);
					if(distance <= closestDistance) {
						// System.out.println("[" + x + "," + y + "," + z + "] "
						// + distance + " -> " + closestDistance);
						int id = world.getBlockIdAt(location);
						int idAbove = world.getBlockIdAt(location.offset(0, 1, 0));
						int idBelow = world.getBlockIdAt(location.offset(0, -1, 0));
						int metadata = world.getBlockMetadataAt(location);

						boolean pumpkinWatermelonDirt = false;
						boolean plantSeeds = true;
						int farmType = actualFarmType;
						if(farmType <= 3 && (id == 104 || id == 105) && hasHoe) {
							BlockLocation[] locations = new BlockLocation[] { location.offset(-1, -1, 0), location.offset(1, -1, 0), location.offset(0, -1, -1), location.offset(0, -1, 1) };
							for(BlockLocation dirtLocation : locations)
								if(world.getBlockIdAt(dirtLocation) == 3 && world.getBlockIdAt(dirtLocation.offset(0, 1, 0)) == 0)
									pumpkinWatermelonDirt = true;
						}
						if(farmType <= 1 && (id == 3 && idAbove == 0 && hasHoe && hasSeeds)) {
							BlockLocation[] locations = new BlockLocation[] { location.offset(-1, 0, 0), location.offset(1, 0, 0), location.offset(0, 0, -1), location.offset(0, 0, 1) };
							for(BlockLocation adjacent : locations) {
								int adjacentId = world.getBlockIdAt(adjacent);
								if(adjacentId == 104 || adjacentId == 105)
									plantSeeds = false;
							}
						}
						if(farmType <= 3 && (pumpkinWatermelonDirt || id == 103 || id == 86 || (id == 115 && metadata > 2) || (id == 59 && metadata > 6) || (id == 83 && idBelow == 83 && idAbove == 83))) {
							farmType = 3;
						} else if(farmType <= 2 && ((id == 88 && idAbove == 0 && hasNetherwarts) || (id == 60 && idAbove == 0 && hasSeeds)))
							farmType = 2;
						// else if(farmType < 2
						// && ((id == 3 || id == 12) && idAbove == 0 &&
						// hasReeds))
						// farmType = 2;
						else if(farmType <= 1 && (id == 3 && idAbove == 0 && hasHoe && hasSeeds && plantSeeds))
							farmType = 1;
						else
							continue;
						if(distance == closestDistance) {
							if(farmType != actualFarmType)
								continue;
						} else
							closest.clear();
						closest.add(location);
						actualFarmType = farmType;
						closestDistance = distance;
					}
				}
			}
		}
		BlockLocation closestLocation = null;
		if(closest.size() > 0)
			closestLocation = closest.get((int) (Math.random() * closest.size()));
		return closestLocation;
	}

	@SuppressWarnings("unused")
	private BlockLocation getClosestBlock(int id, int radius) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return null;
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player.getZ() - 0.5)));
		BlockLocation closest = null;
		long closestDistance = Long.MAX_VALUE;
		for(BlockLocation location : getBlocks(id, radius)) {
			long distance = ourLocation.getDistanceToSquared(location);
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
		BlockLocation ourLocation = new BlockLocation(player.getLocation());
		BlockArea region = this.region;
		if(region != null && !region.contains(ourLocation))
			region = null;
		List<BlockLocation> blocks = new ArrayList<BlockLocation>();
		for(int x = region != null ? region.getX() - ourLocation.getX() : -radius; x < (region != null ? region.getX() + region.getWidth() - ourLocation.getX() : radius); x++) {
			for(int y = region != null ? region.getY() - ourLocation.getY() : -radius / 2; y < (region != null ? region.getY() + region.getHeight() - ourLocation.getY() : radius / 2); y++) {
				for(int z = region != null ? region.getZ() - ourLocation.getZ() : -radius; z < (region != null ? region.getZ() + region.getLength() - ourLocation.getZ() : radius); z++) {
					BlockLocation location = new BlockLocation(ourLocation.getX() + x, ourLocation.getY() + y, ourLocation.getZ() + z);
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
		if(!player.switchHeldItems(slot))
			return false;
		if(player.placeBlock(location))
			return true;
		return false;
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

	public StorageAction getStorageAction() {
		return storageAction;
	}

	public void setStorageAction(StorageAction storageAction) {
		this.storageAction = storageAction;
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
		return "[<x1> <y1> <z1> <x2> <y2> <z2>]";
	}
}
