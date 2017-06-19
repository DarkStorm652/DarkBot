package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet202PlayerAbilities extends AbstractPacket implements ReadablePacket, WriteablePacket {
	public boolean disableDamage;
	public boolean flying;
	public boolean allowFlying;
	public boolean creativeMode;

	private float flySpeed, walkSpeed;

	public Packet202PlayerAbilities() {
		disableDamage = false;
		flying = false;
		allowFlying = false;
		creativeMode = false;
	}

	// public Packet202PlayerAbilities(PlayerCapabilities
	// par1PlayerCapabilities) {
	// field_50072_a = false;
	// field_50070_b = false;
	// field_50071_c = false;
	// field_50069_d = false;
	// field_50072_a = par1PlayerCapabilities.disableDamage;
	// field_50070_b = par1PlayerCapabilities.isFlying;
	// field_50071_c = par1PlayerCapabilities.allowFlying;
	// field_50069_d = par1PlayerCapabilities.isCreativeMode;
	// }

	@Override
	public void readData(DataInputStream in) throws IOException {
		byte flags = in.readByte();
		disableDamage = (flags & 1) > 0;
		flying = (flags & 2) > 0;
		allowFlying = (flags & 4) > 0;
		creativeMode = (flags & 8) > 0;

		flySpeed = in.readFloat();
		walkSpeed = in.readFloat();
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		byte flags = 0;
		if(disableDamage)
			flags |= 1;
		if(flying)
			flags |= 2;
		if(allowFlying)
			flags |= 4;
		if(creativeMode)
			flags |= 8;
		out.writeByte(flags);

		out.writeFloat(flySpeed);
		out.writeFloat(walkSpeed);
	}

	@Override
	public int getId() {
		return 202;
	}
}
