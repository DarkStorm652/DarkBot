package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.FollowTask;
import org.darkstorm.darkbot.minecraftbot.util.Util;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;

public class FollowCommand extends AbstractCommand {

	public FollowCommand(MinecraftBotWrapper bot) {
		super(bot, "follow", "Follow a player or yourself", "[player]", "([\\w]{1,16})?");
	}

	@Override
	public void execute(String[] args) {
		FollowTask followTask = bot.getTaskManager().getTaskFor(FollowTask.class);
		if(followTask.isActive())
			followTask.stop();
		for(Entity entity : bot.getWorld().getEntities()) {
			if(entity instanceof PlayerEntity && isFollowable(args, ((PlayerEntity) entity).getName())) {
				followTask.follow(entity);
				controller.say("Now following " + (args.length > 0 ? Util.stripColors(((PlayerEntity) entity).getName()) : "you") + ".");
				return;
			}
		}
		if(args.length > 0)
			controller.say("Player " + args[0] + " not found.");
		else
			controller.say("Owner not found.");
	}

	private boolean isFollowable(String[] args, String name) {
		name = Util.stripColors(name);
		if(args.length > 0)
			return args[0].equalsIgnoreCase(name);
		for(String owner : controller.getOwners())
			if(owner.equalsIgnoreCase(name))
				return true;
		return false;
	}
}
