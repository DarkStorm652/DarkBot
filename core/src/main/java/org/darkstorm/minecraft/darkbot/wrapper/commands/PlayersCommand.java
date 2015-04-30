package org.darkstorm.minecraft.darkbot.wrapper.commands;

import java.util.*;

import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.event.protocol.server.*;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class PlayersCommand extends AbstractCommand implements EventListener {
	private final List<String> users = new ArrayList<>();

	public PlayersCommand(MinecraftBotWrapper bot) {
		super(bot, "players", "List all players on the server");
	}

	@Override
	public void execute(String[] args) {
		String players;
		synchronized(users) {
			players = users.toString();
		}
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
	public void onPlayerListUpdate(PlayerListUpdateEvent event) {
		synchronized(users) {
			if(!users.contains(event.getPlayerName()))
				users.add(event.getPlayerName());
		}
	}

	@EventHandler
	public void onPlayerListRemove(PlayerListRemoveEvent event) {
		synchronized(users) {
			users.remove(event.getPlayerName());
		}
	}
}
