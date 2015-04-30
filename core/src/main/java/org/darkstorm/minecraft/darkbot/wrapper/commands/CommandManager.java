package org.darkstorm.minecraft.darkbot.wrapper.commands;

public interface CommandManager {
	public void register(Command command);

	public void unregister(Command command);

	public void execute(String descriptor) throws CommandException;

	public Command[] getCommands();
}
