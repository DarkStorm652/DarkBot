package org.darkstorm.darkbot.ircbot.util;

import java.util.*;

import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.UserMessage;

public class Tools {
	private Tools() {
	}

	public static String getCorrectTarget(UserMessage message) {
		if(message == null)
			throw new NullPointerException();
		String receiver = message.getReceiver();
		String sender = message.getSender().getNickname();
		return Channel.isChannel(receiver) ? receiver : sender;
	}

	public static <T> T getIgnoreCase(Map<String, T> list, String string) {
		for(String key : list.keySet())
			if(string.equalsIgnoreCase(key))
				return list.get(key);
		return null;
	}

	public static boolean containsIgnoreCase(Collection<String> list,
			String string) {
		for(String listElement : list)
			if(string.equalsIgnoreCase(listElement))
				return true;
		return false;
	}

	public static boolean removeIgnoreCase(Collection<String> list,
			String string) {
		for(String listElement : list) {
			if(string.equalsIgnoreCase(listElement)) {
				list.remove(listElement);
				return true;
			}
		}
		return false;
	}

	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch(NumberFormatException exception) {
			return false;
		}
	}

	/**
	 * A convenient method that accepts an IP address represented as a long and
	 * returns an integer array of size 4 representing the same IP address.
	 * 
	 * @since PircBot 0.9.4
	 * @param address
	 *            the long value representing the IP address.
	 * @return An int[] of size 4.
	 */
	public static int[] longToIp(long address) {
		int[] ip = new int[4];
		for(int i = 3; i >= 0; i--) {
			ip[i] = (int) (address % 256);
			address = address / 256;
		}
		return ip;
	}

	/**
	 * A convenient method that accepts an IP address represented by a byte[] of
	 * size 4 and returns this as a long representation of the same IP address.
	 * 
	 * @since PircBot 0.9.4
	 * @param address
	 *            the byte[] of size 4 representing the IP address.
	 * @return a long representation of the IP address.
	 */
	public static long ipToLong(byte[] address) {
		if(address.length != 4) {
			throw new IllegalArgumentException("byte array must be of length 4");
		}
		long ipNum = 0;
		long multiplier = 1;
		for(int i = 3; i >= 0; i--) {
			int byteVal = (address[i] + 256) % 256;
			ipNum += byteVal * multiplier;
			multiplier *= 256;
		}
		return ipNum;
	}
}
