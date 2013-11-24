package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet2Handshake extends AbstractPacket implements WriteablePacket {
	public int protocolVersion;
	public String username;
	public String serverHost;
	public int serverPort;

	public Packet2Handshake() {
	}

	public Packet2Handshake(int protocolVersion, String username, String serverHost, int serverPort) {
		this.protocolVersion = protocolVersion;
		this.username = username;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(protocolVersion);
		writeString(username, out);
		writeString(serverHost, out);
		out.writeInt(serverPort);
	}

	@Override
	public int getId() {
		return 2;
	}
}
