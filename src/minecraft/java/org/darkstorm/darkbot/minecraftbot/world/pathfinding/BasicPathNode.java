package org.darkstorm.darkbot.minecraftbot.world.pathfinding;

import org.darkstorm.darkbot.minecraftbot.world.WorldLocation;

public class BasicPathNode implements PathNode {
	private final WorldLocation location;
	private final PathSearch source;
	private PathNode previous, next;
	private double gScore = Integer.MAX_VALUE, fScore = Integer.MAX_VALUE;

	public BasicPathNode(PathSearch source, WorldLocation location) {
		this(source, location, null, null);
	}

	public BasicPathNode(PathSearch source, WorldLocation location,
			PathNode previous, PathNode next) {
		this.location = location;
		this.source = source;
		this.previous = previous;
		this.next = next;
	}

	@Override
	public WorldLocation getLocation() {
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
	public double getGScore() {
		return gScore;
	}

	@Override
	public double getFScore() {
		return fScore;
	}

	@Override
	public void setGScore(double gScore) {
		this.gScore = gScore;
	}

	@Override
	public void setFScore(double fScore) {
		this.fScore = fScore;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PathNode
				&& ((PathNode) obj).getLocation().equals(location);
	}

	@Override
	public String toString() {
		return location.toString() + " G=" + gScore + " F=" + fScore;
	}
}
