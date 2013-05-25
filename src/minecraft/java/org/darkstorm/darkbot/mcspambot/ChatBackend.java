package org.darkstorm.darkbot.mcspambot;

import org.darkstorm.darkbot.mcspambot.commands.CommandException;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet3Chat;
import org.darkstorm.darkbot.minecraftbot.util.Util;

class ChatBackend implements Backend, EventListener {
	private final DarkBotMC mcBot;

	public ChatBackend(DarkBotMC mcBot) {
		this.mcBot = mcBot;
		MinecraftBot bot = mcBot.getBot();
		bot.getEventManager().registerListener(this);
	}

	@Override
	public void say(String message) {
		mcBot.getBot().say(message);
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet3Chat) {
			Packet3Chat chatPacket = (Packet3Chat) packet;
			String message = Util.stripColors(chatPacket.message);
			int index = message.indexOf(mcBot.getOwner());
			if(index == -1)
				return;
			message = message.substring(index + mcBot.getOwner().length());
			index = message.indexOf('!');
			if(index == -1)
				return;
			message = message.substring(index + 1);
			try {
				mcBot.getCommandManager().execute(message);
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
				mcBot.getBot().say(error.toString());
			}
		}
	}

	public DarkBotMC getMCBot() {
		return mcBot;
	}
}
