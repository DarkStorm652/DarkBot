package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;
import java.util.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet209SetPlayerTeam extends AbstractPacket implements
		ReadablePacket {
	private String string1 = "";
	private String string2 = "";
	private String string3 = "";
	private String string4 = "";
	private List<String> list = new ArrayList<String>();
	private int int1 = 0;
	private int int2;

	@Override
	public void readData(DataInputStream in) throws IOException {
		string1 = readString(in, 16);
		int1 = in.readByte();

		if(int1 == 0 || int1 == 2) {
			string2 = readString(in, 32);
			string3 = readString(in, 16);
			string4 = readString(in, 16);
			int2 = in.readByte();
		}

		if(int1 == 0 || int1 == 3 || int1 == 4) {
			short length = in.readShort();

			for(int i = 0; i < length; i++)
				list.add(readString(in, 16));
		}
	}

	@Override
	public int getId() {
		return 209;
	}

	public String getString1() {
		return string1;
	}

	public String getString2() {
		return string2;
	}

	public String getString3() {
		return string3;
	}

	public String getString4() {
		return string4;
	}

	public int getInt1() {
		return int1;
	}

	public int getInt2() {
		return int2;
	}

	public List<String> getList() {
		return list;
	}
}
