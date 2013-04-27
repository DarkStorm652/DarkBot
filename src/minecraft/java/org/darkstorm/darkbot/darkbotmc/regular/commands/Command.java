package org.darkstorm.darkbot.darkbotmc.regular.commands;

import org.darkstorm.darkbot.darkbotmc.regular.RegularBot;

public interface Command {
	public String getName();

	public String getDescription();

	public String getUsage();

	public boolean execute(RegularBot bot, String[] args);
}
