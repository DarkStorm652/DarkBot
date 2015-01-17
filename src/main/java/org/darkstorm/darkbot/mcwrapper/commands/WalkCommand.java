package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.ai.WalkActivity;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;

public class WalkCommand extends AbstractCommand {

	public WalkCommand(MinecraftBotWrapper bot) {
		super(bot, "walk", "Walk to coordinates within the loaded world, with + to indicate relative movement", "<[+]x> [y] <[+]z>", "[+]?[-]?[0-9]+ ([0-9]+ )?[+]?[-]?[0-9]+");
	}

	@Override
	public void execute(String[] args) {
		MainPlayerEntity player = bot.getPlayer();
		BlockLocation location = new BlockLocation(player.getLocation());
		boolean relativeX = args[0].charAt(0) == '+', relativeZ = args[args.length - 1].charAt(0) == '+';
		int x, y = -1, z;

		if(relativeX)
			x = location.getX() + Integer.parseInt(args[0].substring(1));
		else
			x = Integer.parseInt(args[0]);

		if(relativeZ)
			z = location.getZ() + Integer.parseInt(args[args.length - 1].substring(1));
		else
			z = Integer.parseInt(args[args.length - 1]);

		if(args.length < 3) {
			World world = bot.getWorld();
			int botY = (int) Math.floor(bot.getPlayer().getY());
			for(int ty = botY + 1; ty > 0; ty--) {
				if(!world.isColliding(player.getBoundingBoxAt(x + 0.5, ty, z + 0.5))) {
					y = ty;
					break;
				}
			}
			if(y == -1) {
				for(int ty = botY; ty < 256; ty++) {
					if(!world.isColliding(player.getBoundingBoxAt(x + 0.5, ty, z + 0.5))) {
						y = ty;
						break;
					}
				}
			}
			if(y <= 0) {
				controller.say("No appropriate walkable y value!");
				return;
			}
		} else
			y = Integer.parseInt(args[1]);

		BlockLocation target = new BlockLocation(x, y, z);
		bot.setActivity(new WalkActivity(bot, target));
		controller.say("Walking to (" + x + ", " + y + ", " + z + ").");
	}
}
