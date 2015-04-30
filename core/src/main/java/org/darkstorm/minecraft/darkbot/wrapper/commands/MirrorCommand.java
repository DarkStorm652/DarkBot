package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.MirrorTask;
import org.darkstorm.minecraft.darkbot.util.Util;
import org.darkstorm.minecraft.darkbot.world.entity.*;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class MirrorCommand extends AbstractCommand {

	public MirrorCommand(MinecraftBotWrapper bot) {
		super(bot, "mirror", "Copy someone's movements", "[player]", "([\\w]{1,16})?");
	}

	@Override
	public void execute(String[] args) {
		MirrorTask task = bot.getTaskManager().getTaskFor(MirrorTask.class);
		if(task.isActive())
			task.stop();
		
		task.filter(new MirrorTask.Filter() {
			@Override
			public boolean canAttack(LivingEntity entity) {
				if(entity instanceof PlayerEntity)
					return !isOwner(((PlayerEntity) entity).getName());
				return true;
			}
		});
		for(Entity entity : bot.getWorld().getEntities()) {
			if(entity instanceof PlayerEntity && isFollowable(args, ((PlayerEntity) entity).getName())) {
				task.mirror((PlayerEntity) entity);
				controller.say("Now mirroring " + (args.length > 0 ? Util.stripColors(((PlayerEntity) entity).getName()) : "you") + ".");
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
		return isOwner(name);
	}
	
	private boolean isOwner(String name) {
		name = Util.stripColors(name);
		for(String owner : controller.getOwners())
			if(owner.equalsIgnoreCase(name))
				return true;
		return false;
	}
}
