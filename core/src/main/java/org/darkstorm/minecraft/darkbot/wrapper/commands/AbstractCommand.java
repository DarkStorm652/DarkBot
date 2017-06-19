package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.*;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public abstract class AbstractCommand implements Command {
	protected final MinecraftBotWrapper controller;
	protected final MinecraftBotImpl bot;

	private final String name, description, optionDescription, optionRegex;

	public AbstractCommand(MinecraftBotWrapper bot, String name, String description) {
		this(bot, name, description, "", "");
	}

	public AbstractCommand(MinecraftBotWrapper bot, String name, String description, String optionDescription, String optionRegex) {
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

	public MinecraftBotWrapper getBot() {
		return controller;
	}
}
