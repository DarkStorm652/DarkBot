package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public interface PathNode {
	public BlockLocation getLocation();

	public PathNode getNext();
	public PathNode getPrevious();
	public void setNext(PathNode node);
	public void setPrevious(PathNode node);

	public double getCost();
	public double getCostEstimate();
	public void setCost(double cost);
	public void setCostEstimate(double costEstimate);

	public boolean isStart();
	public boolean isEnd();

	public PathSearch getSource();
}
