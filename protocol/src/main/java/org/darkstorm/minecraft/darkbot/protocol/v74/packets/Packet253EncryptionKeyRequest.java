package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;
import java.security.*;

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