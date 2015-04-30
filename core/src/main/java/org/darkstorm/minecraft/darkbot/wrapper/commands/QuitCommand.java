package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.event.protocol.client.RequestDisconnectEvent;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class QuitCommand extends AbstractCommand {

	public QuitCommand(MinecraftBotWrapper bot) {
		super(bot, "quit", "Leave the server");
	}

	@Override
	public void execute(String[] args) {
		controller.say("Leaving!");
		bot.getEventBus().fire(new RequestDisconnectEvent("Quit"));
	}
}
