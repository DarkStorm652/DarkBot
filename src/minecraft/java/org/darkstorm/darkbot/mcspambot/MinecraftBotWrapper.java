package org.darkstorm.darkbot.mcspambot;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.*;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.mcspambot.commands.*;
import org.darkstorm.darkbot.minecraftbot.*;
import org.darkstorm.darkbot.minecraftbot.ai.TaskManager;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.events.world.SpawnEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.Packet8UpdateHealth;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet205ClientCommand;
import org.darkstorm.darkbot.minecraftbot.util.Util;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public abstract class MinecraftBotWrapper implements EventListener {
	private static final DarkBot darkbot = new DarkBot();

	protected final MinecraftBot bot;
	protected final CommandManager commandManager;

	private final List<Backend> backends = new CopyOnWriteArrayList<>();
	private final List<String> owners = new CopyOnWriteArrayList<>();

	public MinecraftBotWrapper(MinecraftBotData data) {
		System.out.println("[" + data.getUsername() + "] Connecting...");
		bot = new MinecraftBot(darkbot, data);
		System.out.println("[" + data.getUsername() + "] Joined!");
		commandManager = new BasicCommandManager(this);
		bot.getEventManager().registerListener(this);
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		Packet packet = event.getPacket();
		switch(packet.getId()) {
		case 0:
			connectionHandler.sendPacket(new Packet0KeepAlive(new Random().nextInt()));
			break;
		case 3:
			String message = ((Packet3Chat) packet).message;
			message = Util.stripColors(message);
			System.out.println("[" + bot.getSession().getUsername() + "] " + message);
			String nocheat = "Please type '([^']*)' to continue sending messages/commands\\.";
			Matcher nocheatMatcher = Pattern.compile(nocheat).matcher(message);
			if(nocheatMatcher.matches()) {
				try {
					String captcha = nocheatMatcher.group(1);
					connectionHandler.sendPacket(new Packet3Chat(captcha));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} else if(message.startsWith("/uc ")) {
				connectionHandler.sendPacket(new Packet3Chat(message));
			} else if(message.contains("has requested to teleport to you.")) {
				for(String owner : owners) {
					if(message.contains(owner)) {
						bot.say("/tpaccept");
						break;
					}
				}
			}
			break;
		case 8:
			Packet8UpdateHealth updateHealth = (Packet8UpdateHealth) packet;
			if(updateHealth.healthMP <= 0)
				connectionHandler.sendPacket(new Packet205ClientCommand(1));
			break;
		case 9:
			TaskManager taskManager = bot.getTaskManager();
			taskManager.stopAll();
			bot.setActivity(null);
			break;
		}
	}

	@EventHandler
	public void onSpawn(SpawnEvent event) {
		MainPlayerEntity player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		inventory.setDelay(250);
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		System.out.println("[" + bot.getSession().getUsername() + "] Disconnected: " + event.getReason());
	}

	public void say(String message) {
		for(Backend backend : backends)
			backend.say(message);
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public String[] getOwners() {
		return (String[]) owners.toArray();
	}

	public void addOwner(String owner) {
		owners.add(owner);
	}

	public void removeOwner(String owner) {
		owners.remove(owner);
	}

	public boolean isOwner(String username) {
		for(String owner : owners)
			if(owner.equals(username))
				return true;
		return false;
	}

	public Backend[] getBackends() {
		return (Backend[]) backends.toArray();
	}

	public void addBackend(Backend backend) {
		backend.enable();
		backends.add(backend);
	}

	public void removeBackend(Backend backend) {
		backends.remove(backend);
		backend.disable();
	}

	public MinecraftBot getBot() {
		return bot;
	}

	public static DarkBot getDarkBot() {
		return darkbot;
	}
}