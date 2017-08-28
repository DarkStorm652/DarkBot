package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.world.PlayerInfo;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class PlayersCommand extends AbstractCommand implements EventListener {
	public PlayersCommand(MinecraftBotWrapper bot) {
		super(bot, "players", "List all players on the server");
	}

	@Override
	public void execute(String[] args) {
		controller.say("Players:");
		PlayerInfo[] players = bot.getWorld().getPlayers();
		for(PlayerInfo player : players)
			controller.say(player.getPlayerName());
	}
}
