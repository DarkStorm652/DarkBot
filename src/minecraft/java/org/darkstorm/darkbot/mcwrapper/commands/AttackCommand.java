package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
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
				controller.say("Attacking "
						+ Util.stripColors(((PlayerEntity) entity).getName())
						+ "!");
				return;
			}
		}
		controller.say("Player " + name + " not found.");
	}
}
