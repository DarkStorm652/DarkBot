package org.darkstorm.darkbot.mcspambot.commands;

public interface Command {
	public String getName();

	public String getDescription();

	public String getOptionDescription();

	public String getOptionRegex();

	public void execute(String[] args);
}
