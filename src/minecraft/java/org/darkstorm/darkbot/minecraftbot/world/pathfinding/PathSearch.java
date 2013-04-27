package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.WorldLocation;

public interface PathSearch {
	public void step();

	public boolean isDone();

	public WorldLocation getStart();

	public WorldLocation getEnd();

	public PathNode getPath();

	public PathSearchProvider getSource();
}
