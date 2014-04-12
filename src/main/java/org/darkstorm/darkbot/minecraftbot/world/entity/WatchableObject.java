package org.darkstorm.darkbot.minecraftbot.world.entity;

public class WatchableObject {
	private final int id, type;
	private Object object;

	public WatchableObject(int type, int id, Object object) {
		this.id = id;
		this.type = type;
		this.object = object;
	}

	public int getDataValueId() {
		return id;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public int getObjectType() {
		return type;
	}

	public void setWatching(boolean watching) {
	}

	@Override
	public String toString() {
		return id + " (" + type + "): " + (object == null ? "null" : object.toString());
	}
}
