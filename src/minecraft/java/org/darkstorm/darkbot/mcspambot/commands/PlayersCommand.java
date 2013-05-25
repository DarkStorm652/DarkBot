package org.darkstorm.darkbot.mcspambot.commands;

import java.util.*;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.Packet201PlayerInfo;

public class PlayersCommand extends AbstractCommand implements EventListener {
	private final List<String> users = new ArrayList<>();

	public PlayersCommand(DarkBotMC bot) {
		super(bot, "players", "List all players on the server");
	}

	@Override
	public void execute(String[] args) {
		String players = users.toString();
		players = players.substring(1, players.length() - 1);
		List<String> lines = new ArrayList<String>();
		String[] parts = players.split(", ");
		String current = "";
		for(int i = 0; i < parts.length; i++) {
			if(current.length() + parts[i].length() + 2 >= 100) {
				lines.add(current);
				current = parts[i] + ", ";
			} else
				current += parts[i] + ", ";
		}
		if(!current.isEmpty()) {
			current = current.substring(0, current.length() - 2);
			lines.add(current);
		}

		bot.say("Players:");
		for(String line : lines)
			bot.say(line);
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		if(event.getPacket().getId() != 201)
			return;
		Packet201PlayerInfo infoPacket = (Packet201PlayerInfo) event
				.getPacket();
		if(infoPacket.isConnected && !users.contains(infoPacket.playerName)) {
			users.add(infoPacket.playerName);
			if(infoPacket.ping == 1000) {
				if(infoPacket.playerName.equalsIgnoreCase(bot.getSession()
						.getUsername()))
					return;
			}
		} else if(!infoPacket.isConnected
				&& users.contains(infoPacket.playerName))
			users.remove(infoPacket.playerName);
	}
}
