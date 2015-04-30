package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.*;
import org.darkstorm.minecraft.darkbot.event.EventBus;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;
import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class InteractCommand extends AbstractCommand {

	public InteractCommand(MinecraftBotWrapper bot) {
		super(bot, "interact", "Interact with a block", "<hit|break|use> <x> <y> <z>", "(?i)(hit|break|use) [-]?[0-9]+ [-]?[0-9]+ [-]?[0-9]+");
	}

	@Override
	public void execute(String[] args) {
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);
		MainPlayerEntity player = bot.getPlayer();
		EventBus eventBus = bot.getEventBus();

		if(args[0].equalsIgnoreCase("hit")) {
			player.face(x, y, z);
			eventBus.fire(new PlayerRotateEvent(player));
			eventBus.fire(new ArmSwingEvent());
			eventBus.fire(new BlockBreakStartEvent(x, y, z, 0));
		} else if(args[0].equalsIgnoreCase("break")) // Non-blocking
			new BlockBreakActivity(bot, new BlockLocation(x, y, z));
		else if(args[0].equalsIgnoreCase("use"))
			new BlockPlaceActivity(bot, new BlockLocation(x, y, z));
	}
}
