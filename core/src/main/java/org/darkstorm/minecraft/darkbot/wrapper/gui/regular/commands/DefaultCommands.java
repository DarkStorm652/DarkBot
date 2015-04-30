package org.darkstorm.minecraft.darkbot.wrapper.gui.regular.commands;

import java.util.*;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.ai.*;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.entity.*;
import org.darkstorm.minecraft.darkbot.wrapper.gui.regular.RegularBot;

public enum DefaultCommands implements Command {
	AI("ai", "Various task controls.",
			"ai <<task> [option]... | stop <task> | info <task> | list>") {
		@Override
		public boolean execute(RegularBot bot, String[] args) {
			if(args.length < 1)
				return false;
			MinecraftBot mcBot = bot.getBot();
			TaskManager taskManager = mcBot.getTaskManager();
			if(args[0].equalsIgnoreCase("list")) {
				List<Task> tasks = taskManager.getRegisteredTasks();
				String message = "[BOT] Registered Tasks: ";
				if(tasks.size() > 0) {
					message += tasks.get(0).getName();
					for(int i = 1; i < tasks.size(); i++)
						message += ", " + tasks.get(i).getName();
				}
				bot.log(message);
				return true;
			}
			for(Task task : taskManager.getRegisteredTasks()) {
				String taskName = task.getName();
				if(args.length > 1 && args[1].equalsIgnoreCase(taskName)) {
					if(args[0].equalsIgnoreCase("info")) {
						bot.log("[BOT] Task "
								+ taskName
								+ " currently "
								+ (task.isActive() ? "active" : "inactive")
								+ ", "
								+ (task.isExclusive() ? "excludes"
										: "does not interfere with")
								+ " other tasks, "
								+ (task.ignoresExclusive() ? "ignores task exclusion"
										: "pauses while other tasks run") + ".");
						if(!task.isActive()) {
							String optionDescription = task
									.getOptionDescription();
							bot.log("[BOT] Use \"ai "
									+ taskName
									+ (optionDescription.isEmpty() ? "" : " "
											+ optionDescription)
									+ "\" to start it.");
						} else
							bot.log("[BOT] Use \"ai stop " + taskName
									+ "\" to stop it.");
						break;
					} else if(args[0].equalsIgnoreCase("stop")) {
						if(task.isActive()) {
							task.stop();
							bot.log("[BOT] Task " + taskName + " disabled.");
						} else
							bot.log("[BOT] Task " + taskName
									+ " already inactive.");
						break;
					}
				}
				if(args[0].equalsIgnoreCase(taskName)) {
					if(!task.isActive()) {
						String[] options = new String[args.length - 1];
						for(int i = 1; i < args.length; i++)
							options[i - 1] = args[i];
						if(task.start(options))
							bot.log("[BOT] Task " + taskName + " enabled.");
						else
							bot.log("[BOT] Task " + taskName
									+ " could not be enabled.");
					} else
						bot.log("[BOT] Task " + taskName + " already active.");
					break;
				}
			}
			return true;
		}
	},
	SAY("say", "Say something in chat.", "say <message>") {
		@Override
		public boolean execute(RegularBot bot, String[] args) {
			if(args.length < 1)
				return false;
			String message = args[0];
			for(int i = 1; i < args.length; i++)
				message += " " + args[i];
			bot.getBot().say(message);
			return true;
		}
	},
	ENTITIES("entities", "List entities nearby.",
			"entities [players|mobs|animals|items|other]") {
		@Override
		public boolean execute(RegularBot bot, String[] args) {
			boolean[] entities = new boolean[5];
			if(args.length == 1)
				if(args[0].equalsIgnoreCase("players"))
					entities[0] = true;
				else if(args[0].equalsIgnoreCase("mobs"))
					entities[1] = true;
				else if(args[0].equalsIgnoreCase("animals"))
					entities[2] = true;
				else if(args[0].equalsIgnoreCase("items"))
					entities[3] = true;
				else if(args[0].equalsIgnoreCase("other"))
					entities[4] = true;
				else
					return false;
			else if(args.length > 1)
				return false;
			else
				Arrays.fill(entities, true);

			MainPlayerEntity player = bot.getBot().getPlayer();
			World world = bot.getBot().getWorld();
			if(player == null || world == null)
				return false;
			bot.log("[BOT] Entities nearby:");
			for(Entity entity : world.getEntities())
				if(entities[0] && entity instanceof PlayerEntity)
					bot.log("[BOT]  - Player: "
							+ ((PlayerEntity) entity).getName() + " ["
							+ player.getDistanceTo(entity) + " blocks away]");
				else if(entities[1] && entity instanceof AggressiveEntity)
					bot.log("[BOT]  - Mob: "
							+ entity.getClass().getSimpleName() + " ["
							+ player.getDistanceTo(entity) + " blocks away]");
				else if(entities[2] && entity instanceof PassiveEntity)
					bot.log("[BOT]  - Animal: "
							+ entity.getClass().getSimpleName() + " ["
							+ player.getDistanceTo(entity) + " blocks away]");
				else if(entities[3] && entity instanceof ItemEntity)
					bot.log("[BOT]  - Item: " + ((ItemEntity) entity).getItem()
							+ " [" + player.getDistanceTo(entity)
							+ " blocks away]");
				else if(entities[4]
						&& !(entity instanceof PlayerEntity
								|| entity instanceof AggressiveEntity
								|| entity instanceof PassiveEntity || entity instanceof ItemEntity))
					bot.log("[BOT]  - Other: "
							+ entity.getClass().getSimpleName() + " ["
							+ player.getDistanceTo(entity) + " blocks away]");
			return true;
		}
	};

	private final String name, description, usage;

	private DefaultCommands(String name, String description, String usage) {
		this.name = name;
		this.description = description;
		this.usage = usage;
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
	public String getUsage() {
		return usage;
	}

	@Override
	public abstract boolean execute(RegularBot bot, String[] args);
}
