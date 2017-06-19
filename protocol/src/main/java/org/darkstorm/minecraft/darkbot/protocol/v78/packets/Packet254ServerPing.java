package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet254ServerPing extends AbstractPacket implements WriteablePacket {
	public int protocolVersion;
	public String hostname;
	public int port;

	public Packet254ServerPing() {
	}

	public Packet254ServerPing(int protocolVersion, String hostname, int port) {
		this.protocolVersion = protocolVersion;
		this.hostname = hostname;
		this.port = port;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(1);
		out.write(250);
		writeString("MC|PingHost", out);
		out.writeShort((3 + (2 * hostname.length())) + 4);
		out.write(protocolVersion);
		writeString(hostname, out);
		out.writeInt(port);
	}

	@Override
	public int getId() {
		return 254;
	}
}
