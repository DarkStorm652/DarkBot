package org.darkstorm.darkbot.minecraftbot.protocol.bidirectional;

import java.io.*;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet252SharedKey extends AbstractPacket implements
		ReadablePacket, WriteablePacket {
	public byte[] field_73307_a = new byte[0];
	public byte[] field_73305_b = new byte[0];
	public SecretKey sharedKey;

	public Packet252SharedKey() {
	}

	public Packet252SharedKey(SecretKey par1SecretKey, PublicKey par2PublicKey,
			byte[] par3ArrayOfByte) {
		sharedKey = par1SecretKey;
		field_73307_a = CryptManager.cipherMethod1(par2PublicKey,
				par1SecretKey.getEncoded());
		field_73305_b = CryptManager.cipherMethod1(par2PublicKey,
				par3ArrayOfByte);
	}

	public void readData(DataInputStream in) throws IOException {
		field_73307_a = readByteArray(in);
		field_73305_b = readByteArray(in);
	}

	public void writeData(DataOutputStream out) throws IOException {
		writeByteArray(field_73307_a, out);
		writeByteArray(field_73305_b, out);
	}

	@Override
	public int getId() {
		return 252;
	}
}