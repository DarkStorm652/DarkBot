package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.events.protocol.client.RequestDisconnectEvent;

public class QuitCommand extends AbstractCommand {

	public QuitCommand(MinecraftBotWrapper bot) {
		super(bot, "quit", "Leave the server");
	}

	@Override
	public void execute(String[] args) {
		controller.say("/r " + "Leaving!");
		bot.getEventManager().sendEvent(new RequestDisconnectEvent("Quit"));
	}
}
