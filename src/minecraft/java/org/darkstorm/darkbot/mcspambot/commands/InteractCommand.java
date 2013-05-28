package org.darkstorm.darkbot.mcspambot.commands;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.protocol.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.PlayerInventory;

public class InteractCommand extends AbstractCommand {

	public InteractCommand(DarkBotMC bot) {
		super(bot, "interact", "Interact with a block",
				"<hit|break|use> <x> <y> <z>",
				"(?i)(hit|break|use) [-]?[0-9]+ [-]?[0-9]+ [-]?[0-9]+");
	}

	@Override
	public void execute(String[] args) {
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);
		MainPlayerEntity player = bot.getPlayer();
		PlayerInventory inventory = player.getInventory();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		player.face(x, y, z);
		connectionHandler.sendPacket(new Packet12PlayerLook((float) player
				.getYaw(), (float) player.getPitch(), true));
		connectionHandler.sendPacket(new Packet18Animation(player.getId(),
				Animation.SWING_ARM));

		if(args[0].equalsIgnoreCase("hit")) {
			connectionHandler.sendPacket(new Packet14BlockDig(0, x, y, z, 0));
		} else if(args[0].equalsIgnoreCase("break")) {
			connectionHandler.sendPacket(new Packet14BlockDig(0, x, y, z, 0));
			connectionHandler.sendPacket(new Packet14BlockDig(2, x, y, z, 0));
		} else if(args[0].equalsIgnoreCase("use")) {
			Packet15Place placePacket = new Packet15Place();
			placePacket.xPosition = x;
			placePacket.yPosition = y + 1;
			placePacket.zPosition = z;
			placePacket.direction = 0;
			placePacket.itemStack = inventory.getCurrentHeldItem();
			connectionHandler.sendPacket(placePacket);
		}
	}
}
