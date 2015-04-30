package org.darkstorm.minecraft.darkbot.wrapper.gui.spam;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.wrapper.gui.spam.ActionProvider.Action;

public interface ActionManager {
	public void start();

	public void stop();

	public void restart();

	public boolean isRunning();

	public Action[] getActions();

	public void setActions(Action... actions);

	public MinecraftBot getBot();
}
