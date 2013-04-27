package org.darkstorm.darkbot.ircbot.commands;

import org.darkstorm.darkbot.bot.Nameable;
import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.Message;
import org.darkstorm.darkbot.ircbot.logging.IRCLogger;

public abstract class IRCCommand implements Nameable {
	protected IRCBot bot;
	protected IRCLogger logger;
	protected CommandHandler commandHandler;

	protected boolean enabled = true;

	public IRCCommand(CommandHandler commandHandler) {
		if(commandHandler == null)
			throw new NullPointerException();
		bot = commandHandler.getBot();
		logger = bot.getLogger();
		this.commandHandler = commandHandler;
	}

	public abstract void execute(Message message);

	public abstract String getName();

	public abstract String getDescription();

	public String getCommandName() {
		return null;
	}

	public String getUsage() {
		return "";
	}

	public Permissions getPermissions() {
		return Permissions.ALL;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public IRCBot getBot() {
		return bot;
	}
}
