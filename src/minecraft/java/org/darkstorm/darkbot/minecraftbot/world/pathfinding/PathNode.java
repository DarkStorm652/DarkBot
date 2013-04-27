package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.WorldLocation;

public interface PathNode {
	public WorldLocation getLocation();

	public PathNode getNext();

	public PathNode getPrevious();

	public double getGScore();

	public double getFScore();

	public void setNext(PathNode node);

	public void setPrevious(PathNode node);

	public void setGScore(double gScore);

	public void setFScore(double fScore);

	public boolean isStart();

	public boolean isEnd();

	public PathSearch getSource();
}
