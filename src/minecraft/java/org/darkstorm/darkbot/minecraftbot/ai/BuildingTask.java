package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet15Place;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

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
	private WalkTask walkTask;
	private EatTask eatTask;
	private boolean running = false;

	private int ticksWait, blockId;
	private BlockLocation startCounterLocation = null;
	private BlockLocation point1, point2;

	public BuildingTask(final MinecraftBot bot) {
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
		blockId = Integer.parseInt(options[0]);
		point1 = new BlockLocation(Integer.parseInt(options[1]),
				Integer.parseInt(options[2]), Integer.parseInt(options[3]));
		point2 = new BlockLocation(Integer.parseInt(options[4]),
				Integer.parseInt(options[5]), Integer.parseInt(options[6]));
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
		if(walkTask.isActive() || eatTask.isActive())
			return;
		if(ticksWait > 0) {
			ticksWait--;
			return;
		}
		World world = bot.getWorld();
		MainPlayerEntity player = bot.getPlayer();
		System.out.println("Building!");
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player
				.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player
				.getZ() - 0.5)));
		boolean flip = false;
		for(int y = point1.getY(); y <= point2.getY(); y++) {
			for(int x = Math.min(point1.getX(), point2.getX()); x <= Math.max(
					point1.getX(), point2.getX()); x++) {
				int z1 = Math.min(point1.getZ(), point2.getZ());
				int z2 = Math.max(point1.getZ(), point2.getZ());
				if(flip) {
					int temp = z1;
					z1 = z2;
					z2 = temp;
				}
				for(int z = z1; flip ? z >= z2 : z <= z2; z += flip ? -1 : 1) {
					if(startCounterLocation != null
							&& startCounterLocation.getX() == x
							&& startCounterLocation.getY() == y
							&& startCounterLocation.getZ() == z)
						continue;
					int id = world.getBlockIdAt(x, y, z);
					if(!BlockType.getById(id).isSolid()) {
						if(startCounterLocation != null) {
							BlockLocation location = new BlockLocation(x, y, z);
							System.out.println(ourLocation + " -> " + location);
							BlockLocation original = location;
							BlockLocation below = location.offset(0, -1, 0);
							while(!BlockType.getById(world.getBlockIdAt(below))
									.isSolid()
									&& !world.getPathFinder().getHeuristic()
											.isClimbableBlock(below)) {
								location = below;
								below = below.offset(0, -1, 0);
								if(original.getY() - location.getY() >= 5)
									return;
							}
							if(ourLocation.getX() != location.getX()
									|| ourLocation.getY() != location.getY()
									|| ourLocation.getZ() != location.getZ()) {
								walkTask.setTarget(location);
								ticksWait = 4;
								return;
							}
							placeBlockAt(startCounterLocation, blockId);

							startCounterLocation = null;
						}

						startCounterLocation = new BlockLocation(x, y, z);
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

	private boolean placeBlockAt(BlockLocation location, int id) {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		PlayerInventory inventory = player.getInventory();
		int slot = -1;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item != null && item.getId() == id) {
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
		bot.updateMovement();
		player.swingArm();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
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
