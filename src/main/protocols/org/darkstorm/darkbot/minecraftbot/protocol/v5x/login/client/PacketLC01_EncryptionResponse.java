package org.darkstorm.darkbot.minecraftbot.protocol.v5x.login.client;

import java.io.*;
import java.security.*;

import javax.crypto.SecretKey;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketLC01_EncryptionResponse extends AbstractPacketX implements WriteablePacket {
	private final SecretKey secretKey;
	private final PublicKey publicKey;
	private final byte[] sharedSecret, verifyToken;

	public PacketLC01_EncryptionResponse(SecretKey secretKey, PublicKey publicKey, byte[] verifyToken) {
		super(0x01, State.LOGIN, Direction.UPSTREAM);

		this.secretKey = secretKey;
		this.publicKey = publicKey;

		try {
			sharedSecret = EncryptionUtil.cipher(1, publicKey, secretKey.getEncoded());
			this.verifyToken = EncryptionUtil.cipher(1, publicKey, verifyToken);
		} catch(GeneralSecurityException exception) {
			throw new Error("Unable to cipher", exception);
		}
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeByteArray(sharedSecret, out);
		writeByteArray(verifyToken, out);
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public byte[] getSharedSecret() {
		return sharedSecret;
	}

	public byte[] getVerifyToken() {
		return verifyToken;
	}
}
