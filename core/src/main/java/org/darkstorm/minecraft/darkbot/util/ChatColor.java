package org.darkstorm.minecraft.darkbot.util;

import java.util.regex.Pattern;

public enum ChatColor {
	BLACK('0', 0x00, Type.COLOR),
	DARK_BLUE('1', 0x01, Type.COLOR),
	DARK_GREEN('2', 0x02, Type.COLOR),
	DARK_AQUA('3', 0x03, Type.COLOR),
	DARK_RED('4', 0x04, Type.COLOR),
	DARK_PURPLE('5', 0x05, Type.COLOR),
	GOLD('6', 0x06, Type.COLOR),
	GRAY('7', 0x07, Type.COLOR),
	DARK_GRAY('8', 0x08, Type.COLOR),
	BLUE('9', 0x09, Type.COLOR),
	GREEN('a', 0x0A, Type.COLOR),
	AQUA('b', 0x0B, Type.COLOR),
	RED('c', 0x0C, Type.COLOR),
	LIGHT_PURPLE('d', 0x0D, Type.COLOR),
	YELLOW('e', 0x0E, Type.COLOR),
	WHITE('f', 0x0F, Type.COLOR),

	OBFUSCATED('k', 0x10, Type.FORMAT),
	BOLD('l', 0x11, Type.FORMAT),
	STRIKETHROUGH('m', 0x12, Type.FORMAT),
	UNDERLINE('n', 0x13, Type.FORMAT),
	ITALIC('o', 0x14, Type.FORMAT),

	RESET('r', 0x15, Type.RESET);

	public static final char COLOR_CODE = '\u00A7';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile(COLOR_CODE + "[0-9A-FK-ORa-fk-or]");
	private static final IntHashMap<ChatColor> CODE_MAPPINGS, ID_MAPPINGS;

	private final char code;
	private final int id;
	private final Type type;

	private final String stringValue;

	private ChatColor(char code, int id, Type type) {
		this.code = code;
		this.id = id;
		this.type = type;

		stringValue = new String(new char[] { COLOR_CODE, code });
	}

	public char getCode() {
		return code;
	}

	public int getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return stringValue;
	}

	public static ChatColor fromCode(char code) {
		return CODE_MAPPINGS.get(code);
	}

	public static ChatColor fromId(int id) {
		return ID_MAPPINGS.get(id);
	}

	public static String stripColor(final String input) {
		if(input == null)
			throw new NullPointerException();
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	static {
		CODE_MAPPINGS = new IntHashMap<>(values().length);
		ID_MAPPINGS = new IntHashMap<>(values().length);

		for(ChatColor color : values()) {
			CODE_MAPPINGS.put(color.code, color);
			ID_MAPPINGS.put(color.id, color);
		}
	}

	public enum Type {
		COLOR,
		FORMAT,
		RESET
	}
}