package org.darkstorm.minecraft.darkbot.wrapper.cli;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.darkstorm.minecraft.darkbot.MinecraftBotImpl;
import org.darkstorm.minecraft.darkbot.ai.*;
import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;
import org.darkstorm.minecraft.darkbot.wrapper.backend.ChatBackend;
import org.darkstorm.minecraft.darkbot.wrapper.commands.*;

import java.io.IOException;
import java.util.Arrays;

public class CLIBotWrapper extends MinecraftBotWrapper {
	private CLIBotWrapper(MinecraftBotImpl bot, String owner) {
		super(bot);
		addOwner(owner);
		addBackend(new ChatBackend(this));

		TaskManager taskManager = bot.getTaskManager();
		taskManager.registerTask(new FollowTask(bot));
		taskManager.registerTask(new DefendTask(bot));
		taskManager.registerTask(new AttackTask(bot));
		taskManager.registerTask(new HostileTask(bot));
		taskManager.registerTask(new EatTask(bot));
		taskManager.registerTask(new MiningTask(bot));
		taskManager.registerTask(new FishingTask(bot));
		taskManager.registerTask(new FarmingTask(bot));
		taskManager.registerTask(new BuildingTask(bot));
		taskManager.registerTask(new AvoidDeathTask(bot));
		taskManager.registerTask(new DestroyingTask(bot));
		taskManager.registerTask(new DerpTask(bot));
		taskManager.registerTask(new TwerkTask(bot));
		taskManager.registerTask(new MirrorTask(bot));

		taskManager.registerTask(new XRayMiningTask(bot));

		commandManager.register(new AttackAllCommand(this));
		commandManager.register(new AttackCommand(this));
		commandManager.register(new BuildCommand(this));
		commandManager.register(new CalcCommand(this));
		commandManager.register(new ChatDelayCommand(this));
		commandManager.register(new DestroyCommand(this));
		commandManager.register(new DropAllCommand(this));
		commandManager.register(new DropCommand(this));
		commandManager.register(new DropIdCommand(this));
		commandManager.register(new EquipCommand(this));
		commandManager.register(new FarmCommand(this));
		commandManager.register(new FishCommand(this));
		commandManager.register(new FollowCommand(this));
		commandManager.register(new HelpCommand(this));
		commandManager.register(new InteractCommand(this));
		commandManager.register(new MineCommand(this));
		commandManager.register(new OwnerCommand(this));
		commandManager.register(new PlayersCommand(this));
		commandManager.register(new QuitCommand(this));
		commandManager.register(new SayCommand(this));
		commandManager.register(new SetWalkCommand(this));
		commandManager.register(new StatusCommand(this));
		commandManager.register(new StopCommand(this));
		commandManager.register(new SwitchCommand(this));
		commandManager.register(new ToolCommand(this));
		commandManager.register(new WalkCommand(this));
		commandManager.register(new DerpCommand(this));
		commandManager.register(new TwerkCommand(this));
		commandManager.register(new CrouchCommand(this));
		commandManager.register(new MirrorCommand(this));
		commandManager.register(new MuteCommand(this));

		commandManager.register(new LoginCommand(this));
		commandManager.register(new ServerSelectCommand(this));
		commandManager.register(new XRayMineCommand(this));
	}

	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("h", "help"), "Show this help dialog.");
		OptionSpec<String> serverOption = parser.acceptsAll(Arrays.asList("s", "server"), "Server to join.").withRequiredArg().describedAs("server-address[:port]");
		OptionSpec<String> ownerOption = parser.acceptsAll(Arrays.asList("o", "owner"), "Owner of the bot (username of in-game control).").withRequiredArg().describedAs("username");
		OptionSpec<String> usernameOption = parser.acceptsAll(Arrays.asList("u", "username"), "Bot username. Ignored in presence of 'account-list'.").withRequiredArg().describedAs("username/email");
		OptionSpec<?> autoRejoinOption = parser.acceptsAll(Arrays.asList("a", "auto-rejoin"), "Auto-rejoin a server on disconnect.");

		OptionSet options = CLIWrapperUtils.parseOptions(parser, args);

		if(options.has("help")) {
            CLIWrapperUtils.printHelp(parser);
			return;
		}

		if (!options.has(usernameOption)) {
			System.out.println("Option 'username' required.");
			return;
		}

		if(!options.has(serverOption)) {
			System.out.println("Option 'server' required.");
			return;
		}

		final String server = options.valueOf(serverOption);
		final String owner = CLIWrapperUtils.getRequiredOption(options, ownerOption);
		final String username = options.valueOf(usernameOption);
		final boolean autoRejoin = options.has(autoRejoinOption);

		while(true) {
			try {
				CLIBotWrapper bot = new CLIBotWrapper(createBot(server, username), owner);
				while (bot.getBot().isConnected()) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException exception) {
						exception.printStackTrace();
					}
				}
				if (!autoRejoin)
					break;
			} catch (Exception exception) {
				System.out.println("[Bot] Error connecting: " + exception.toString());
				exception.printStackTrace();
			}
		}

		System.exit(0);
	}

	private static MinecraftBotImpl createBot(String server, String username) throws IOException {
		MinecraftBotImpl.Builder builder = MinecraftBotImpl.builder();
		builder.username(username);

		if(server != null && !server.isEmpty()) {
			int port = 25565;
			if(server.contains(":")) {
				String[] parts = server.split(":");
				server = parts[0];
				port = Integer.parseInt(parts[1]);
			}
			builder.server(server).port(port);
		} else
			throw new IllegalArgumentException("Unknown server!");

		return builder.build();
	}
}