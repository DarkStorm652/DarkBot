package org.darkstorm.darkbot.minecraftbot.protocol.v5x.login.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

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
