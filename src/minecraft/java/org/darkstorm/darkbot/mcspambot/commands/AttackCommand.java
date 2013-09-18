package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.AttackTask;
import org.darkstorm.darkbot.minecraftbot.util.Util;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;

public class AttackCommand extends AbstractCommand {

	public AttackCommand(MinecraftBotWrapper bot) {
		super(bot, "attack", "Attack a player by name", "<name>", "[\\w]{1,16}");
	}

	@Override
	public void execute(String[] args) {
		String name = args[0];
		AttackTask attackTask = bot.getTaskManager().getTaskFor(
				AttackTask.class);
		for(Entity entity : bot.getWorld().getEntities()) {
			if(entity instanceof PlayerEntity
					&& Util.stripColors(((PlayerEntity) entity).getName())
							.equalsIgnoreCase(name)) {
				attackTask.setAttackEntity(entity);
				controller.say("/r " + "Attacking "
						+ Util.stripColors(((PlayerEntity) entity).getName())
						+ "!");
				return;
			}
		}
		controller.say("/r " + "Player " + name + " not found.");
	}
}
