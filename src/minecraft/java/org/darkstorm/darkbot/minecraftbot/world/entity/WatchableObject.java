package org.darkstorm.darkbot.minecraftbot.world.entity;

public class WatchableObject {
	private final int objectType;

	/** id of max 31 */
	private final int dataValueId;
	private Object watchedObject;

	// private boolean isWatching;

	public WatchableObject(int par1, int par2, Object par3Obj) {
		dataValueId = par2;
		watchedObject = par3Obj;
		objectType = par1;
		// isWatching = true;
	}

	public int getDataValueId() {
		return dataValueId;
	}

	public void setObject(Object par1Obj) {
		watchedObject = par1Obj;
	}

	public Object getObject() {
		return watchedObject;
	}

	public int getObjectType() {
		return objectType;
	}

	public void setWatching(boolean par1) {
		// isWatching = par1;
	}

	@Override
	public String toString() {
		return watchedObject == null ? "null" : watchedObject.toString();
	}
}
