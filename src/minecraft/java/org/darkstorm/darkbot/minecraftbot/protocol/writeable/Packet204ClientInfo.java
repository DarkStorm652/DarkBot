package org.darkstorm.darkbot.minecraftbot.protocol.writeable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet204ClientInfo extends AbstractPacket implements
		WriteablePacket {
	public String language;
	public int renderDistance;
	public int chatVisisble;
	public boolean chatColours;
	public int gameDifficulty;
	public boolean showCape;

	public Packet204ClientInfo(String par1Str, int par2, int par3,
			boolean par4, int par5, boolean par6) {
		language = par1Str;
		renderDistance = par2;
		chatVisisble = par3;
		chatColours = par4;
		gameDifficulty = par5;
		showCape = par6;
	}

	public void writeData(DataOutputStream out) throws IOException {
		writeString(language, out);
		out.writeByte(renderDistance);
		out.writeByte(chatVisisble | (chatColours ? 1 : 0) << 3);
		out.writeByte(gameDifficulty);
		out.writeBoolean(showCape);
	}

	@Override
	public int getId() {
		return 204;
	}
}