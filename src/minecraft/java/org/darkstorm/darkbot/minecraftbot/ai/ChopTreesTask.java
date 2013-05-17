package org.darkstorm.darkbot.minecraftbot.ai;

import java.util.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.world.*;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class ChopTreesTask implements Task, EventListener {
	private static final BlockLocation[] surrounding = new BlockLocation[] {
			new BlockLocation(-1, 0, 1), new BlockLocation(0, 0, 1),
			new BlockLocation(1, 0, 1), new BlockLocation(-1, 0, 0),
			new BlockLocation(1, 0, 0), new BlockLocation(-1, 0, -1),
			new BlockLocation(0, 0, -1), new BlockLocation(1, 0, -1), };

	private final MinecraftBot bot;
	private final List<BlockLocation> logPositions = new ArrayList<BlockLocation>();
	private boolean running = false;
	private final Comparator<BlockLocation> logComparator;

	private BlockLocation currentlyChopping = null, lastTarget = null;
	private int ticksSinceChop = 0;

	public ChopTreesTask(final MinecraftBot bot) {
		this.bot = bot;
		logComparator = new Comparator<BlockLocation>() {
			@Override
			public int compare(BlockLocation o1, BlockLocation o2) {
				MainPlayerEntity player = bot.getPlayer();
				int distance1 = player.getDistanceToSquared(o1.getX(),
						o1.getY(), o1.getZ());
				int distance2 = player.getDistanceToSquared(o2.getX(),
						o2.getY(), o2.getZ());
				return Double.compare(distance1, distance2);
			}
		};
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
		currentlyChopping = null;
	}

	@Override
	public synchronized void run() {
		if(currentlyChopping != null) {
			ticksSinceChop++;
			if(ticksSinceChop > 500) {
				logPositions.remove(currentlyChopping);
				currentlyChopping = null;
			}
			return;
		}
		ticksSinceChop = 0;
		if(logPositions.size() == 0)
			return;
		Collections.sort(logPositions, logComparator);
		System.out.println("Chopping!");
		BlockLocation closest = logPositions.get(0);
		World world = bot.getWorld();
		MainPlayerEntity player = bot.getPlayer();
		if(player.getDistanceTo(closest) > 64)
			return;
		if(player.getDistanceToSquared(closest.getX(), closest.getY() + 1,
				closest.getZ()) < 16) {
			player.face(closest.getX(), closest.getY(), closest.getZ());
			ConnectionHandler connectionHandler = bot.getConnectionHandler();
			connectionHandler.sendPacket(new Packet12PlayerLook((float) player
					.getYaw(), (float) player.getPitch(), true));
			connectionHandler.sendPacket(new Packet18Animation(player.getId(),
					Animation.SWING_ARM));
			connectionHandler.sendPacket(new Packet14BlockDig(0,
					closest.getX(), closest.getY(), closest.getZ(), 0));
			connectionHandler.sendPacket(new Packet14BlockDig(2,
					closest.getX(), closest.getY(), closest.getZ(), 0));
			currentlyChopping = closest;
		} else {
			if(lastTarget != null && lastTarget.equals(closest)) {
				logPositions.remove(closest);
				return;
			}
			lastTarget = closest;
			BlockLocation[] sortedSurrounding = Arrays.copyOf(surrounding,
					surrounding.length);
			for(int i = 0; i < sortedSurrounding.length; i++) {
				BlockLocation adjacent = sortedSurrounding[i];
				sortedSurrounding[i] = new BlockLocation(closest.getX()
						+ adjacent.getX(), closest.getY() + adjacent.getY(),
						closest.getZ() + adjacent.getZ());
			}
			Arrays.sort(sortedSurrounding, logComparator);
			for(BlockLocation newLocation : sortedSurrounding) {
				if(!BlockType.getById(
						world.getBlockIdAt(newLocation.getX(),
								newLocation.getY(), newLocation.getZ()))
						.isSolid()) {
					int yChange = 0;
					while(!BlockType
							.getById(
									world.getBlockIdAt(newLocation.getX(),
											newLocation.getY() - 1,
											newLocation.getZ())).isSolid()
							&& newLocation.getY() >= 0 && yChange < 5) {
						newLocation = new BlockLocation(newLocation.getX(),
								newLocation.getY() - 1, newLocation.getZ());
						yChange++;
					}
					if(yChange == 5
							|| BlockType.getById(
									world.getBlockIdAt(newLocation.getX(),
											newLocation.getY() + 1,
											newLocation.getZ())).isSolid())
						continue;
					bot.setActivity(new WalkActivity(bot, newLocation));
					return;
				}
			}
			logPositions.remove(closest);
		}
	}

	@Override
	public synchronized boolean isActive() {
		return running;
	}

	@EventHandler
	public synchronized void onChunkLoad(ChunkLoadEvent event) {
		Chunk chunk = event.getChunk();
		BlockLocation chunkLocation = new BlockLocation(chunk.getLocation());
		for(int x = 0; x < 16; x++) {
			for(int y = 0; y < 16; y++) {
				for(int z = 0; z < 16; z++) {
					if(chunk.getBlockIdAt(x, y, z) == 17) {
						BlockLocation location = new BlockLocation(
								chunkLocation.getX() + x, chunkLocation.getY()
										+ y, chunkLocation.getZ() + z);
						if(!logPositions.contains(location))
							logPositions.add(location);
					}
				}
			}
		}
		Collections.sort(logPositions, logComparator);
	}

	@EventHandler
	public synchronized void onBlockChange(BlockChangeEvent event) {
		BlockLocation location = event.getLocation();
		Block newBlock = event.getNewBlock();
		if((event.getOldBlock() == null && newBlock == null)
				|| (event.getOldBlock() != null && newBlock != null && event
						.getOldBlock().getId() == newBlock.getId()))
			return;
		if(logPositions.contains(location)) {
			if(newBlock == null || newBlock.getId() == 0) {
				System.out.println(newBlock == null ? "Air" : event
						.getOldBlock().getId()
						+ ","
						+ event.getOldBlock().getMetadata()
						+ " -> "
						+ newBlock.getId() + "," + newBlock.getMetadata());
				logPositions.remove(location);
				if(currentlyChopping != null
						&& currentlyChopping.equals(location)) {
					currentlyChopping = null;
					System.out.println("No longer chopping");
				}
			}
		}
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
		return "Chop";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
