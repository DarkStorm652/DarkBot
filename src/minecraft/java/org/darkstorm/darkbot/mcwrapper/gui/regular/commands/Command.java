package org.darkstorm.darkbot.mcwrapper.gui.regular.commands;

import org.darkstorm.darkbot.mcwrapper.gui.regular.RegularBot;

public interface Command {
	public String getName();

	public String getDescription();

	public String getUsage();

	public boolean execute(RegularBot bot, String[] args);
}
