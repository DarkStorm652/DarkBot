package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;
import java.security.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet253EncryptionKeyRequest extends AbstractPacket implements ReadablePacket {
	public String serverId;
	public PublicKey publicKey;
	public byte[] verifyToken;

	public Packet253EncryptionKeyRequest() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		serverId = readString(in, 20);
		try {
			publicKey = EncryptionUtil.generatePublicKey(readByteArray(in));
		} catch(GeneralSecurityException exception) {
			throw new Error("Unable to generate public key", exception);
		}
		verifyToken = readByteArray(in);
	}

	@Override
	public int getId() {
		return 253;
	}
}