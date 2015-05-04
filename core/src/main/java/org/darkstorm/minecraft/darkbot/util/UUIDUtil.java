package org.darkstorm.minecraft.darkbot.util;

import java.security.*;
import java.util.UUID;

public final class UUIDUtil {
	private UUIDUtil() {
	}
	
	public static UUID generateSystemUUID() {
		return generateSystemUUID(null);
	}

	public static UUID generateSystemUUID(String extra) {
		StringBuffer digestData = new StringBuffer();
		digestData.append(System.getProperty("os.name"));
		digestData.append(System.getProperty("os.version"));
		digestData.append(System.getProperty("os.arch"));
		digestData.append(System.getProperty("user.name"));
		digestData.append(System.getProperty("user.home"));
		digestData.append(System.getProperty("java.version"));
		digestData.append(System.getProperty("java.home"));
		if(extra != null)
			digestData.append(extra);
		return generateHashUUID(digestData.toString());
	}

	public static UUID generateHashUUID(String digestData) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch(NoSuchAlgorithmException exception) {
			return UUID.randomUUID();
		}

		md5.update(digestData.getBytes());
		byte[] data = md5.digest();

		StringBuffer hash = new StringBuffer();
		for(int i = 0; i < data.length; i++) {
			byte b = data[i];

			if((b & 0xF0) == 0)
				hash.append(0);
			hash.append(Integer.toHexString(b & 0xFF));
		}

		StringBuffer uuid = new StringBuffer();
		uuid.append(hash.substring(0, 8)).append('-');
		uuid.append(hash.substring(8, 12)).append('-');
		uuid.append(hash.substring(12, 16)).append('-');
		uuid.append(hash.substring(16, 20)).append('-');
		uuid.append(hash.substring(20, 32));
		return UUID.fromString(uuid.toString());
	}
}
