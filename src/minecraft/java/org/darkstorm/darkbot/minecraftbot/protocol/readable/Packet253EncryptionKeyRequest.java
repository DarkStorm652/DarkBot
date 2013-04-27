package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;
import java.security.PublicKey;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet253EncryptionKeyRequest extends AbstractPacket implements
		ReadablePacket {
	public String serverId;
	public PublicKey publicKey;
	public byte[] verifyToken;

	public Packet253EncryptionKeyRequest() {
	}

	public void readData(DataInputStream in) throws IOException {
		serverId = readString(in, 20);
		publicKey = CryptManager.generatePublicKey(readByteArray(in));
		verifyToken = readByteArray(in);
	}

	@Override
	public int getId() {
		return 253;
	}
}