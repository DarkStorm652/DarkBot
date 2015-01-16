package org.darkstorm.darkbot.mcwrapper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.*;

import org.darkstorm.darkbot.mcwrapper.backend.Backend;
import org.darkstorm.darkbot.mcwrapper.commands.*;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.ai.TaskManager;
import org.darkstorm.darkbot.minecraftbot.event.*;
import org.darkstorm.darkbot.minecraftbot.event.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.event.protocol.client.RequestRespawnEvent;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.*;
import org.darkstorm.darkbot.minecraftbot.event.world.SpawnEvent;
import org.darkstorm.darkbot.minecraftbot.util.Util;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public abstract class MinecraftBotWrapper implements EventListener {
	protected final MinecraftBot bot;
	protected final CommandManager commandManager;

	private final List<Backend> backends = new CopyOnWriteArrayList<>();
	private final List<String> owners = new CopyOnWriteArrayList<>();

	public MinecraftBotWrapper(MinecraftBot bot) {
		this.bot = bot;
		
		bot.setMessageDelay(2000);
		bot.setInventoryDelay(15);

		commandManager = new BasicCommandManager(this);
		bot.getEventBus().register(this);
	}

	@EventHandler
	public void onChatReceived(ChatReceivedEvent event) {
		String message = Util.stripColors(event.getMessage());
		System.out.println("[" + bot.getSession().getUsername() + "]> " + message);
		String nocheat = "Please type '([^']*)' to continue sending messages/commands\\.";
		Matcher nocheatMatcher = Pattern.compile(nocheat).matcher(message);
		if(nocheatMatcher.matches()) {
			try {
				String captcha = nocheatMatcher.group(1);
				bot.say(captcha);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		} else if(message.contains("teleport to")) {
			for(String owner : owners) {
				if(message.contains(owner)) {
					bot.say("/tpaccept");
					break;
				}
			}
		} else if(message.startsWith("/uc "))
			bot.say(message);
	}

	@EventHandler
	public void onHealthUpdate(HealthUpdateEvent event) {
		if(event.getHealth() <= 0)
			bot.getEventBus().fire(new RequestRespawnEvent());
	}

	@EventHandler
	public void onRespawn(RespawnEvent event) {
		TaskManager taskManager = bot.getTaskManager();
		taskManager.stopAll();
		bot.setActivity(null);
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
		return owners.toArray(new String[0]);
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

	public void addBackend(Backend backend) {
		backend.enable();
		backends.add(backend);
	}

	public void removeBackend(Backend backend) {
		backends.remove(backend);
		backend.disable();
	}

	public Backend[] getBackends() {
		return backends.toArray(new Backend[backends.size()]);
	}

	public final MinecraftBot getBot() {
		return bot;
	}
}