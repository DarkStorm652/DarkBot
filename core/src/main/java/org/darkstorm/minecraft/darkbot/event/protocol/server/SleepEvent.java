package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

public class SleepEvent extends EntityEvent {
	private final int bedX, bedY, bedZ;

	public SleepEvent(int playerId, int bedX, int bedY, int bedZ) {
		super(playerId);
		this.bedX = bedX;
		this.bedY = bedY;
		this.bedZ = bedZ;
	}

	public int getBedX() {
		return bedX;
	}

	public int getBedY() {
		return bedY;
	}

	public int getBedZ() {
		return bedZ;
	}
}
