package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CryptManager {
	public static final Charset field_75898_a = Charset.forName("ISO_8859_1");

	public static SecretKey generateSecretKey() {
		CipherKeyGenerator var0 = new CipherKeyGenerator();
		var0.init(new KeyGenerationParameters(new SecureRandom(), 128));
		return new SecretKeySpec(var0.generateKey(), "AES");
	}

	public static KeyPair createNewKeyPair() {
		try {
			KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
			var0.initialize(1024);
			return var0.generateKeyPair();
		} catch(NoSuchAlgorithmException var1) {
			var1.printStackTrace();
			System.err.println("Key pair generation failed!");
			return null;
		}
	}

	public static byte[] encrypt(String par0Str, PublicKey par1PublicKey,
			SecretKey par2SecretKey) {
		try {
			return hash(
					"SHA-1",
					new byte[][] { par0Str.getBytes("ISO_8859_1"),
							par2SecretKey.getEncoded(),
							par1PublicKey.getEncoded() });
		} catch(UnsupportedEncodingException var4) {
			var4.printStackTrace();
			return null;
		}
	}

	private static byte[] hash(String algorithm,
			byte[]... data) {
		try {
			MessageDigest var2 = MessageDigest.getInstance(algorithm);
			byte[][] var3 = data;
			int var4 = data.length;

			for(int var5 = 0; var5 < var4; ++var5) {
				byte[] var6 = var3[var5];
				var2.update(var6);
			}

			return var2.digest();
		} catch(NoSuchAlgorithmException var7) {
			var7.printStackTrace();
			return null;
		}
	}

	public static PublicKey generatePublicKey(byte[] par0ArrayOfByte) {
		try {
			X509EncodedKeySpec var1 = new X509EncodedKeySpec(par0ArrayOfByte);
			KeyFactory var2 = KeyFactory.getInstance("RSA");
			return var2.generatePublic(var1);
		} catch(NoSuchAlgorithmException var3) {
			var3.printStackTrace();
		} catch(InvalidKeySpecException var4) {
			var4.printStackTrace();
		}

		System.err.println("Public key reconstitute failed!");
		return null;
	}

	public static SecretKey func_75887_a(PrivateKey par0PrivateKey,
			byte[] par1ArrayOfByte) {
		return new SecretKeySpec(cipherMethod2(par0PrivateKey, par1ArrayOfByte),
				"AES");
	}

	public static byte[] cipherMethod1(Key par0Key, byte[] data) {
		return cipher(1, par0Key, data);
	}

	public static byte[] cipherMethod2(Key par0Key, byte[] data) {
		return cipher(2, par0Key, data);
	}

	private static byte[] cipher(int method, Key key,
			byte[] data) {
		try {
			return createCipher(method, key.getAlgorithm(), key).doFinal(
					data);
		} catch(IllegalBlockSizeException var4) {
			var4.printStackTrace();
		} catch(BadPaddingException var5) {
			var5.printStackTrace();
		}

		System.err.println("Cipher data failed!");
		return null;
	}

	private static Cipher createCipher(int par0, String par1Str, Key par2Key) {
		try {
			Cipher var3 = Cipher.getInstance(par1Str);
			var3.init(par0, par2Key);
			return var3;
		} catch(InvalidKeyException var4) {
			var4.printStackTrace();
		} catch(NoSuchAlgorithmException var5) {
			var5.printStackTrace();
		} catch(NoSuchPaddingException var6) {
			var6.printStackTrace();
		}

		System.err.println("Cipher creation failed!");
		return null;
	}

	private static BufferedBlockCipher func_75892_a(boolean par0, Key par1Key) {
		BufferedBlockCipher var2 = new BufferedBlockCipher(new CFBBlockCipher(
				new AESFastEngine(), 8));
		var2.init(par0,
				new ParametersWithIV(new KeyParameter(par1Key.getEncoded()),
						par1Key.getEncoded(), 0, 16));
		return var2;
	}

	public static OutputStream encryptOuputStream(SecretKey par0SecretKey,
			OutputStream par1OutputStream) {
		return new CipherOutputStream(par1OutputStream, func_75892_a(true,
				par0SecretKey));
	}

	public static InputStream decryptInputStream(SecretKey par0SecretKey,
			InputStream par1InputStream) {
		return new CipherInputStream(par1InputStream, func_75892_a(false,
				par0SecretKey));
	}

	static {
		Security.addProvider(new BouncyCastleProvider());
	}
}