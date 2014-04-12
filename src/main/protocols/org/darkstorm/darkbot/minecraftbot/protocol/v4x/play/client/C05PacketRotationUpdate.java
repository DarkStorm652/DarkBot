package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

public class C05PacketRotationUpdate extends C03PacketPlayerUpdate {
	private double yaw, pitch;

	public C05PacketRotationUpdate(double yaw, double pitch, boolean grounded) {
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
