package org.darkstorm.minecraft.darkbot.wrapper.commands;

@SuppressWarnings("serial")
public class CommandException extends Exception {
	private final Command command;

	public CommandException() {
		command = null;
	}

	public CommandException(String message) {
		super(message);
		command = null;
	}

	public CommandException(Throwable cause) {
		super(cause);
		command = null;
	}

	public CommandException(String message, Throwable cause) {
		super(message, cause);
		command = null;
	}

	public CommandException(Command command) {
		this.command = command;
	}

	public CommandException(Command command, String message) {
		super(message);
		this.command = command;
	}

	public CommandException(Command command, Throwable cause) {
		super(cause);
		this.command = command;
	}

	public CommandException(Command command, String message, Throwable cause) {
		super(message, cause);
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}
}
