package commands;

import java.util.Set;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class DebugCommand extends IRCCommand {

	public DebugCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(message instanceof UserMessage) {
			UserMessage userMessage = (UserMessage) message;
			IRCCommand[] commands = commandHandler.getCommands();
			int enabled = 0;
			for(IRCCommand command : commands)
				if(command.isEnabled())
					enabled++;
			MessageHandler messageHandler = bot.getMessageHandler();
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"[COMMANDS] Loaded: " + commands.length + "\tEnabled: "
							+ enabled);
			Runtime runtime = Runtime.getRuntime();
			long freeMemory = runtime.freeMemory();
			long totalMemory = runtime.totalMemory();
			long usedMemory = totalMemory - freeMemory;
			messageHandler.sendMessage(
					Tools.getCorrectTarget(userMessage),
					"[MEMORY] Used: " + usedMemory + "\tFree: " + freeMemory
							+ "\tTotal: " + totalMemory + "\tMax: "
							+ runtime.maxMemory());
			Set<Thread> threads = Thread.getAllStackTraces().keySet();
			int alive = 0;
			for(Thread thread : threads)
				if(thread.isAlive())
					alive++;
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"[THREADS] Count: " + threads.size() + "\tAlive: " + alive);
		}
	}

	@Override
	public String getName() {
		return "Debug Command";
	}

	@Override
	public String getCommandName() {
		return "DEBUG";
	}

	@Override
	public String getUsage() {
		return "DEBUG";
	}

	@Override
	public String getDescription() {
		return "Shows debug info";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.ORIGINAL_OWNER;
	}

}
