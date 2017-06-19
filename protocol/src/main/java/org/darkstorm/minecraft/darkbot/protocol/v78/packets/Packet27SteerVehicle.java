package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet27SteerVehicle extends AbstractPacket implements WriteablePacket {
	public float sideways, forwards;
	public boolean jump, unmount;

	public Packet27SteerVehicle() {
	}

	public Packet27SteerVehicle(float sideways, float forwards, boolean jump, boolean unmount) {
		this.sideways = sideways;
		this.forwards = forwards;
		this.jump = jump;
		this.unmount = unmount;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeFloat(sideways);
		out.writeFloat(forwards);
		out.writeBoolean(jump);
		out.writeBoolean(unmount);
	}

	@Override
	public int getId() {
		return 27;
	}
}
