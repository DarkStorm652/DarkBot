package org.darkstorm.minecraft.darkbot.protocol.v4x.login.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketLS02_LoginSuccess extends AbstractPacketX implements ReadablePacket {
	private String uuid, username;

	public PacketLS02_LoginSuccess() {
		super(0x02, State.LOGIN, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		uuid = readString(in);
		username = readString(in);
	}

	public String getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}
}
