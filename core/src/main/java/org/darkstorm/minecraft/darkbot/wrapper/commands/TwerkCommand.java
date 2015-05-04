package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.ai.TwerkTask;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class TwerkCommand extends AbstractCommand {

	public TwerkCommand(MinecraftBotWrapper bot) {
		super(bot, "twerk", "wat", "[on/off]", "(?i)(on|off)?");
	}

	@Override
	public void execute(String[] args) {
		TwerkTask task = bot.getTaskManager().getTaskFor(TwerkTask.class);
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("on")) {
				if(!task.isActive())
					task.start(args);
			} else if(task.isActive())
				task.stop();
		} else {
			if(!task.isActive())
				task.start(args);
			else
				task.stop();
		}
	}
}
