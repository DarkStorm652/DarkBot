package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;
import java.security.*;

import javax.crypto.SecretKey;

public class Packet252SharedKey extends AbstractPacket implements ReadablePacket, WriteablePacket {
	public SecretKey sharedKey;
	public byte[] sharedSecret = new byte[0];
	public byte[] verifyToken = new byte[0];

	public Packet252SharedKey() {
	}

	public Packet252SharedKey(SecretKey sharedKey, PublicKey publicKey, byte[] data) {
		this.sharedKey = sharedKey;
		try {
			sharedSecret = EncryptionUtil.cipher(1, publicKey, sharedKey.getEncoded());
			verifyToken = EncryptionUtil.cipher(1, publicKey, data);
		} catch(GeneralSecurityException exception) {
			throw new Error("Unable to cipher", exception);
		}
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		sharedSecret = readByteArray(in);
		verifyToken = readByteArray(in);
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeByteArray(sharedSecret, out);
		writeByteArray(verifyToken, out);
	}

	@Override
	public int getId() {
		return 252;
	}
}