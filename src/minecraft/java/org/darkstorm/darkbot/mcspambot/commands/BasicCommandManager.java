package org.darkstorm.darkbot.mcspambot.commands;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.darkstorm.darkbot.mcspambot.DarkBotMC;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;

public class BasicCommandManager implements CommandManager {
	private final DarkBotMC bot;
	private final List<Command> commands = new CopyOnWriteArrayList<>();

	public BasicCommandManager(DarkBotMC bot) {
		this.bot = bot;
	}

	@Override
	public void register(Command command) {
		commands.add(command);
		if(command instanceof EventListener)
			bot.getBot().getEventManager()
					.registerListener((EventListener) command);
	}

	@Override
	public void unregister(Command command) {
		if(command instanceof EventListener)
			bot.getBot().getEventManager()
					.unregisterListener((EventListener) command);
		commands.remove(command);
	}

	@Override
	public void execute(String descriptor) throws CommandException {
		descriptor = descriptor.trim();
		int spaceIndex = descriptor.indexOf(' ');
		String name, args = "";
		if(spaceIndex != -1)
			args = descriptor.substring(spaceIndex + 1, descriptor.length());
		else
			spaceIndex = descriptor.length();
		name = descriptor.substring(0, spaceIndex);

		String[] argsSplit = new String[0];
		if(!args.isEmpty()) {
			List<String> argsList = new ArrayList<>();
			boolean quoted = false, escaped = false;
			char last = ' ';
			StringBuilder part = null;
			for(char c : args.toCharArray()) {
				if(part == null && last == ' ' && c != ' ') {
					if(c == '\"') {
						quoted = true;
						part = new StringBuilder();
					} else if(c == '\\') {
						escaped = true;
						part = new StringBuilder();
					} else
						part = new StringBuilder().append(c);
				} else if(part != null) {
					if(c == '"' && !escaped) {
						if(quoted) {
							quoted = false;
							argsList.add(part.toString());
							part = null;
						} else if(last != '\\')
							throw new CommandException("Syntax error.");
					} else if(c == '\\' && !escaped) {
						escaped = true;
					} else if(escaped && c != '"' && c != '\\') {
						throw new CommandException("Syntax error.");
					} else if(c == ' ') {
						if(!quoted) {
							argsList.add(part.toString());
							part = null;
						} else
							part.append(c);
					} else {
						escaped = false;
						part.append(c);
					}
				} else if(c != ' ')
					throw new CommandException("Syntax error.");
				last = c;
			}
			if(part != null)
				argsList.add(part.toString());
			if(quoted || escaped)
				throw new CommandException("Syntax error.");
			argsSplit = argsList.toArray(argsSplit);
			if(argsSplit.length > 0) {
				StringBuilder argsBuilder = new StringBuilder(argsSplit[0]);
				for(int i = 1; i < argsSplit.length; i++)
					argsBuilder.append(' ').append(argsSplit[i]);
				args = argsBuilder.toString();
			} else
				args = "";
		}

		for(Command command : commands) {
			if(!name.equalsIgnoreCase(command.getName()))
				continue;
			if(!args.matches(command.getOptionRegex()))
				throw new CommandException(command, "Incorrect command syntax.");
			try {
				command.execute(argsSplit);
			} catch(Exception exception) {
				throw new CommandException(command, exception);
			}
			break;
		}
	}

	@Override
	public Command[] getCommands() {
		return commands.toArray(new Command[commands.size()]);
	}

	public DarkBotMC getBot() {
		return bot;
	}
}
