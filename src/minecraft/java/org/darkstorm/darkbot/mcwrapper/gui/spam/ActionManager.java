package org.darkstorm.darkbot.mcwrapper.gui.spam;

import org.darkstorm.darkbot.mcwrapper.gui.spam.ActionProvider.Action;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;

public interface ActionManager {
	public void start();

	public void stop();

	public void restart();

	public boolean isRunning();

	public Action[] getActions();

	public void setActions(Action... actions);

	public MinecraftBot getBot();
}
