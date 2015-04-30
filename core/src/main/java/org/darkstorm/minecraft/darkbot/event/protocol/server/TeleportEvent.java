package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class TeleportEvent extends ProtocolEvent {
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;

	public TeleportEvent(double x, double y, double z, double stance, float yaw, float pitch) {
		this(x, y, z, yaw, pitch);
	}

	public TeleportEvent(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}
}
