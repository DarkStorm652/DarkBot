package org.darkstorm.minecraft.darkbot.wrapper.commands;

public interface Command {
	public String getName();

	public String getDescription();

	public String getOptionDescription();

	public String getOptionRegex();

	public void execute(String[] args);
}
