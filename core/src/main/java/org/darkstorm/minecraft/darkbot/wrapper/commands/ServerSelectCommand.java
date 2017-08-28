package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.event.EventHandler;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.event.protocol.server.WindowCloseEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.server.WindowOpenEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.server.WindowSlotChangeEvent;
import org.darkstorm.minecraft.darkbot.world.item.Inventory;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class ServerSelectCommand extends AbstractCommand implements EventListener {

	public ServerSelectCommand(MinecraftBotWrapper bot) {
		super(bot, "serverselect", "Selects a server from an in-game window");
	}

	@Override
	public void execute(String[] args) {
		bot.sendChat("/servers");
	}

	private WindowOpenEvent teleportWindow;

	@EventHandler
	public void onWindowOpen(WindowOpenEvent event) {
		if(event.getWindowTitle().equals("{\"text\":\"§c§lServer menu\"}"))
			teleportWindow = event;
	}

	@EventHandler
	public void onWindowSlotChange(WindowSlotChangeEvent event) {
		if(teleportWindow != null && event.getWindowId() == teleportWindow.getWindowId() && event.getNewItem().getId() == 257) {
			Inventory window = bot.getPlayer().getWindow();
			if(window != null) {
					window.selectItemAt(event.getSlot(), true);
			}
		}
	}

	@EventHandler
	public void onWindowClose(WindowCloseEvent event) {
		if(teleportWindow != null && event.getWindowId() == teleportWindow.getWindowId())
			teleportWindow = null;
	}
}
