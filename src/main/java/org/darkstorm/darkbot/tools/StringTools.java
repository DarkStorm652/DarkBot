package org.darkstorm.darkbot.tools;

import java.util.Vector;

public class StringTools {
	private StringTools() {

	}

	public static boolean isInteger(String testString) {
		try {
			Integer.parseInt(testString);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	public static String[] split(String str, String splitStr) {
		String replacer = new String(str.toCharArray());
		Vector<String> strings = new Vector<String>();
		while(replacer.contains(splitStr)) {
			int i = replacer.indexOf(splitStr);
			strings.add(replacer.substring(0, i));
			replacer = replacer.substring(i + splitStr.length());
		}
		strings.add(replacer);
		return strings.toArray(new String[] {});
	}

	public static String[] splitFirst(String str, String match) {
		String first = split(str, match)[0];
		String last = str.substring(first.length() + match.length());
		return new String[] { first, last };
	}

	public static String[] splitLast(String str, String match) {
		String[] split = split(str, match);
		String last = split[split.length - 1];
		String first = str.replace(match + last, "");
		return new String[] { first, last };
	}
}
