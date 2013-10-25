package org.darkstorm.darkbot.mcwrapper.commands;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;

public class OwnerCommand extends AbstractCommand {

	public OwnerCommand(MinecraftBotWrapper bot) {
		super(bot, "owner", "Set, add, or remove a bot owner", "<add|remove|set> <owner>", "(?i)(add|remove|set) [\\w]{1,16}");
	}

	@Override
	public void execute(String[] args) {
		switch(args[0].toLowerCase()) {
		case "add":
			controller.addOwner(args[1]);
			controller.say("Added owner " + args[1] + ".");
			break;
		case "remove":
			if(!controller.isOwner(args[1])) {
				controller.say("No such owner " + args[1] + ".");
			} else if(controller.getOwners().length == 1) {
				controller.say("Must have at least one owner.");
			} else {
				controller.removeOwner(args[1]);
				controller.say("Removed owner " + args[1]);
			}
			break;
		case "set":
			for(String owner : controller.getOwners())
				controller.removeOwner(owner);
			controller.addOwner(args[1]);
			controller.say("Set owner to " + args[1] + ".");
		}
	}
}
