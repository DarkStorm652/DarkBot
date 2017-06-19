package org.darkstorm.minecraft.darkbot.protocol.v5x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;
import org.darkstorm.minecraft.darkbot.world.Difficulty;

import java.io.*;

public class PacketC15_ClientSettings extends AbstractPacketX implements WriteablePacket {
	private String locale;
	private ViewDistance viewDistance;
	private ChatMode chatMode;
	private Difficulty difficulty;
	private boolean showChatColors, showCape;

	public PacketC15_ClientSettings(String locale, ViewDistance viewDistance, ChatMode chatMode, Difficulty difficulty, boolean showChatColors, boolean showCape) {
		super(0x15, State.PLAY, Direction.UPSTREAM);

		this.locale = locale;
		this.viewDistance = viewDistance;
		this.chatMode = chatMode;
		this.difficulty = difficulty;
		this.showChatColors = showChatColors;
		this.showCape = showCape;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeString(locale, out);
		out.write(viewDistance.ordinal());
		out.write(chatMode.ordinal());
		out.writeBoolean(showChatColors);
		out.write(difficulty.getId());
		out.writeBoolean(showCape);
	}

	public String getLocale() {
		return locale;
	}

	public ViewDistance getViewDistance() {
		return viewDistance;
	}

	public ChatMode getChatMode() {
		return chatMode;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public boolean shouldShowChatColors() {
		return showChatColors;
	}

	public boolean shouldShowCape() {
		return showCape;
	}

	public enum ViewDistance {
		FAR,
		NORMAL,
		SHORT,
		TINY
	}

	public enum ChatMode {
		ENABLED,
		COMMANDS_ONLY,
		DISABLED,
	}
}
