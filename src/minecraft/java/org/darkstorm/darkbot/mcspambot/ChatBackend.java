package org.darkstorm.darkbot.mcspambot;

import org.darkstorm.darkbot.mcspambot.commands.CommandException;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet3Chat;
import org.darkstorm.darkbot.minecraftbot.util.Util;

public class ChatBackend implements Backend, EventListener {
	private final MinecraftBotWrapper bot;

	private String activator = "!";

	public ChatBackend(MinecraftBotWrapper bot) {
		this.bot = bot;
	}

	@Override
	public void enable() {
		MinecraftBot mcbot = bot.getBot();
		mcbot.getEventManager().registerListener(this);
	}

	@Override
	public void say(String message) {
		bot.getBot().say(message);
	}

	@Override
	public void disable() {
		MinecraftBot mcbot = bot.getBot();
		mcbot.getEventManager().unregisterListener(this);
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet3Chat) {
			Packet3Chat chatPacket = (Packet3Chat) packet;
			String message = Util.stripColors(chatPacket.message);
			String executor = null;
			for(String owner : bot.getOwners()) {
				int index = message.indexOf(owner);
				if(index == -1)
					continue;
				executor = owner;
			}
			if(executor == null)
				return;
			message = message.substring(message.indexOf(executor) + executor.length());
			int index = message.indexOf(activator);
			if(index == -1)
				return;
			message = message.substring(index + activator.length());
			try {
				bot.getCommandManager().execute(message);
			} catch(CommandException e) {
				StringBuilder error = new StringBuilder("Error: ");
				if(e.getCause() != null)
					error.append(e.getCause().toString());
				else if(e.getMessage() == null)
					error.append("null");
				if(e.getMessage() != null) {
					if(e.getCause() != null)
						error.append(": ");
					error.append(e.getMessage());
				}
				bot.getBot().say(error.toString());
			}
		}
	}

	public String getActivator() {
		return activator;
	}

	public void setActivator(String activator) {
		this.activator = activator;
	}
}
