package org.darkstorm.minecraft.darkbot.wrapper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.*;

import org.darkstorm.minecraft.darkbot.*;
import org.darkstorm.minecraft.darkbot.ai.TaskManager;
import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.general.DisconnectEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.client.RequestRespawnEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.server.*;
import org.darkstorm.minecraft.darkbot.util.ChatColor;
import org.darkstorm.minecraft.darkbot.wrapper.backend.Backend;
import org.darkstorm.minecraft.darkbot.wrapper.commands.*;

public abstract class MinecraftBotWrapper implements EventListener {
	protected final MinecraftBot bot;
	protected final CommandManager commandManager;

	private final List<Backend> backends = new CopyOnWriteArrayList<>();
	private final List<String> owners = new CopyOnWriteArrayList<>();

	public MinecraftBotWrapper(MinecraftBot bot) {
		this.bot = bot;
		
		bot.setMessageDelay(2000);
		bot.setInventoryDelay(4);

		commandManager = new BasicCommandManager(this);
		bot.getEventBus().register(this);
	}

	@EventHandler
	public void onChatReceived(ChatReceivedEvent event) {
		String message = ChatColor.stripColor(event.getMessage());
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