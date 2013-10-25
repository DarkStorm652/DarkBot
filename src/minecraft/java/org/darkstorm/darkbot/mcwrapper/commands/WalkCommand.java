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
		int x, y, z;

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
			for(y = 256; y > 0; y--) {
				int id = world.getBlockIdAt(x, y - 1, z);
				if(BlockType.getById(id).isSolid())
					break;
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
