package org.darkstorm.minecraft.darkbot.ai;

import java.util.*;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.event.world.ChunkLoadEvent;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.*;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public class ChopTreesTask implements Task, EventListener {
	private static final BlockLocation[] surrounding = new BlockLocation[] { new BlockLocation(-1, 0, 1), new BlockLocation(0, 0, 1),
			new BlockLocation(1, 0, 1), new BlockLocation(-1, 0, 0), new BlockLocation(1, 0, 0), new BlockLocation(-1, 0, -1), new BlockLocation(0, 0, -1),
			new BlockLocation(1, 0, -1), };
	private static final int LOG_ID = 17;

	private final MinecraftBot bot;
	private final List<BlockLocation> logPositions = new ArrayList<>();
	private final Comparator<BlockLocation> logComparator;

	private BlockLocation lastTarget = null;
	private boolean running = false;

	public ChopTreesTask(final MinecraftBot bot) {
		this.bot = bot;
		logComparator = new Comparator<BlockLocation>() {
			@Override
			public int compare(BlockLocation o1, BlockLocation o2) {
				MainPlayerEntity player = bot.getPlayer();
				int distance1 = player.getDistanceToSquared(o1.getX(), o1.getY(), o1.getZ());
				int distance2 = player.getDistanceToSquared(o2.getX(), o2.getY(), o2.getZ());
				return Double.compare(distance1, distance2);
			}
		};
		bot.getEventBus().register(this);
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
		World world = bot.getWorld();
		if(!logPositions.isEmpty()) {
			Iterator<BlockLocation> i = logPositions.iterator();
			while(i.hasNext()) {
				BlockLocation location = i.next();
				if(world.getBlockIdAt(location) != LOG_ID)
					i.remove();
			}
		} else
			return;
		Collections.sort(logPositions, logComparator);
		System.out.println("Chopping!");
		BlockLocation closest = logPositions.get(0);
		MainPlayerEntity player = bot.getPlayer();
		if(player.getDistanceTo(closest) > 64)
			return;
		if(player.getDistanceToSquared(closest.getX(), closest.getY() + 1, closest.getZ()) < 16) {
			player.breakBlock(closest);
			return;
		} else {
			if(lastTarget != null && lastTarget.equals(closest)) {
				logPositions.remove(closest);
				return;
			}
			lastTarget = closest;
			BlockLocation[] sortedSurrounding = Arrays.copyOf(surrounding, surrounding.length);
			for(int i = 0; i < sortedSurrounding.length; i++) {
				BlockLocation adjacent = sortedSurrounding[i];
				sortedSurrounding[i] = new BlockLocation(closest.getX() + adjacent.getX(), closest.getY() + adjacent.getY(), closest.getZ() + adjacent.getZ());
			}
			Arrays.sort(sortedSurrounding, logComparator);
			for(BlockLocation newLocation : sortedSurrounding) {
				if(!BlockType.getById(world.getBlockIdAt(newLocation.getX(), newLocation.getY(), newLocation.getZ())).isSolid()) {
					int yChange = 0;
					while(!BlockType.getById(world.getBlockIdAt(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ())).isSolid()
							&& newLocation.getY() >= 0 && yChange < 5) {
						newLocation = new BlockLocation(newLocation.getX(), newLocation.getY() - 1, newLocation.getZ());
						yChange++;
					}
					if(yChange == 5 || BlockType.getById(world.getBlockIdAt(newLocation.getX(), newLocation.getY() + 1, newLocation.getZ())).isSolid())
						continue;
					player.walkTo(newLocation);
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
					if(chunk.getBlockIdAt(x, y, z) == LOG_ID) {
						BlockLocation location = new BlockLocation(chunkLocation.getX() + x, chunkLocation.getY() + y, chunkLocation.getZ() + z);
						if(!logPositions.contains(location))
							logPositions.add(location);
					}
				}
			}
		}
		Collections.sort(logPositions, logComparator);
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
