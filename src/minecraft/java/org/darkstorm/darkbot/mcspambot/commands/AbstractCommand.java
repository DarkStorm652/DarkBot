package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;

public abstract class AbstractCommand implements Command {
	protected final DarkBotMC controller;
	protected final MinecraftBot bot;

	private final String name, description, optionDescription, optionRegex;

	public AbstractCommand(DarkBotMC bot, String name, String description) {
		this(bot, name, description, "", "");
	}

	public AbstractCommand(DarkBotMC bot, String name, String description,
			String optionDescription, String optionRegex) {
		controller = bot;
		this.bot = bot.getBot();
		this.name = name;
		this.description = description;
		this.optionDescription = optionDescription;
		this.optionRegex = optionRegex;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getOptionDescription() {
		return optionDescription;
	}

	@Override
	public String getOptionRegex() {
		return optionRegex;
	}

	public DarkBotMC getBot() {
		return controller;
	}
}
