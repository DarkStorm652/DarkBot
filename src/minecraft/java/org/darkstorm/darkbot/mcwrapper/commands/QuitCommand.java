package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.events.protocol.client.RequestDisconnectEvent;

public class QuitCommand extends AbstractCommand {

	public QuitCommand(MinecraftBotWrapper bot) {
		super(bot, "quit", "Leave the server");
	}

	@Override
	public void execute(String[] args) {
		controller.say("Leaving!");
		bot.getEventManager().sendEvent(new RequestDisconnectEvent("Quit"));
	}
}
