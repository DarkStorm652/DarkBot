package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.ai.FollowTask;
import org.darkstorm.darkbot.minecraftbot.util.Util;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;

public class FollowCommand extends AbstractCommand {

	public FollowCommand(DarkBotMC bot) {
		super(bot, "follow", "Follow a player or yourself", "[player]",
				"([\\w]{1,16})?");
	}

	@Override
	public void execute(String[] args) {
		String name = controller.getOwner();
		if(args.length > 0) {
			name = args[0];
			if(name.equalsIgnoreCase(controller.getOwner())) {
				name = controller.getOwner();
				args = new String[0];
			}
		}
		FollowTask followTask = bot.getTaskManager().getTaskFor(
				FollowTask.class);
		if(followTask.isActive())
			followTask.stop();
		for(Entity entity : bot.getWorld().getEntities()) {
			if(entity instanceof PlayerEntity
					&& Util.stripColors(((PlayerEntity) entity).getName())
							.equalsIgnoreCase(name)) {
				followTask.follow(entity);
				controller.say("Following "
						+ (args.length > 0 ? Util
								.stripColors(((PlayerEntity) entity).getName())
								: "you") + ".");
				return;
			}
		}
		controller.say("Player " + name + " not found.");
	}
}
