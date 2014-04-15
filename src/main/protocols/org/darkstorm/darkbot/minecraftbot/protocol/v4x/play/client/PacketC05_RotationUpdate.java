package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

public class PacketC05_RotationUpdate extends PacketC03_PlayerUpdate {
	private double yaw, pitch;

	public PacketC05_RotationUpdate(double yaw, double pitch, boolean grounded) {
		super(0x05, grounded);

		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeFloat((float) yaw);
		out.writeFloat((float) pitch);

		super.writeData(out);
	}
}
