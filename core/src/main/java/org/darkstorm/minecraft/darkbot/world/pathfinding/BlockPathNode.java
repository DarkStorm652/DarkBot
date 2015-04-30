package org.darkstorm.minecraft.darkbot.world.pathfinding;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public class BlockPathNode implements PathNode {
	private final BlockLocation location;
	private final PathSearch source;

	private PathNode previous, next;

	private double cost = Integer.MAX_VALUE, costEstimate = Integer.MAX_VALUE;

	public BlockPathNode(PathSearch source, BlockLocation location) {
		this(source, location, null, null);
	}

	public BlockPathNode(PathSearch source, BlockLocation location, PathNode previous, PathNode next) {
		this.location = location;
		this.source = source;
		this.previous = previous;
		this.next = next;
	}

	@Override
	public BlockLocation getLocation() {
		return location;
	}

	@Override
	public PathNode getNext() {
		return next;
	}

	@Override
	public PathNode getPrevious() {
		return previous;
	}

	@Override
	public void setNext(PathNode next) {
		this.next = next;
	}

	@Override
	public void setPrevious(PathNode previous) {
		this.previous = previous;
	}

	@Override
	public boolean isStart() {
		return previous == null;
	}

	@Override
	public boolean isEnd() {
		return next == null;
	}

	@Override
	public PathSearch getSource() {
		return source;
	}

	@Override
	public double getCost() {
		return cost;
	}

	@Override
	public double getCostEstimate() {
		return costEstimate;
	}

	@Override
	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public void setCostEstimate(double costEstimate) {
		this.costEstimate = costEstimate;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PathNode && location.equals(((PathNode) obj).getLocation());
	}

	@Override
	public String toString() {
		return location.toString() + " Cost=" + cost + " Estimate=" + costEstimate;
	}
}
