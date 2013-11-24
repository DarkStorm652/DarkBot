package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.WriteablePacket;

public class Packet11PlayerPosition extends Packet10Flying implements WriteablePacket {
	public double x;
	public double y;
	public double z;
	public double stance;

	public Packet11PlayerPosition() {
	}

	public Packet11PlayerPosition(double par1, double par3, double par5, double par7, boolean par9) {
		super(par9);
		x = par1;
		y = par3;
		stance = par5;
		z = par7;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(stance);
		out.writeDouble(z);
		super.writeData(out);
	}

	@Override
	public int getId() {
		return 11;
	}
}
