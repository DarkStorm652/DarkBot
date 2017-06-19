package org.darkstorm.minecraft.darkbot.protocol.v4x.login.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketLS01_EncryptionRequest extends AbstractPacketX implements ReadablePacket {
	private String serverId;
	private byte[] publicKey, verifyToken;

	public PacketLS01_EncryptionRequest() {
		super(0x01, State.LOGIN, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		serverId = readString(in);
		publicKey = readByteArray(in);
		verifyToken = readByteArray(in);

		System.out.println("Read public key: " + byteArrayString(publicKey));
		System.out.println("Read verify token: " + byteArrayString(verifyToken));
	}

	private String byteArrayString(byte[] data) {
		StringBuffer buffer = new StringBuffer();
		for(byte b : data) {
			if(buffer.length() == 0)
				buffer.append("new byte[] { ");
			else
				buffer.append(", ");
			buffer.append("0x");
			if(b <= 0xF)
				buffer.append(0);
			buffer.append(Integer.toHexString(b & 0xFF).toUpperCase());
		}
		return buffer.append(" };").toString();
	}

	public String getServerId() {
		return serverId;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public byte[] getVerifyToken() {
		return verifyToken;
	}
}
