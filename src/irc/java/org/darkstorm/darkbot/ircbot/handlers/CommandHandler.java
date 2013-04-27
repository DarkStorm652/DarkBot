package org.darkstorm.darkbot.ircbot.handlers;

import java.util.*;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.events.*;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.logging.IRCLogger.IRCLogType;
import org.darkstorm.darkbot.ircbot.util.IRCCommandLoader;

public class CommandHandler extends IRCHandler implements MessageListener {
	private String commandActivator = "!";
	private IRCCommandLoader commandLoader;
	private final ArrayList<IRCCommand> commands;
	private Object commandsLock = new Object();

	public CommandHandler(IRCBot bot) {
		super(bot);
		commands = new ArrayList<IRCCommand>();
		commandLoader = new IRCCommandLoader(this);
		commandLoader.loadCommands();
		EventHandler eventHandler = bot.getEventHandler();
		eventHandler.addMessageListener(this);
	}

	public void reloadCommands() {
		commandLoader.loadCommands();
	}

	public void addCommand(IRCCommand command) {
		synchronized(commandsLock) {
			commands.add(command);
		}
	}

	public boolean removeCommand(IRCCommand command) {
		synchronized(commandsLock) {
			return commands.remove(command);
		}
	}

	public void removeAllCommands() {
		synchronized(commandsLock) {
			commands.clear();
		}
	}

	public IRCCommand[] getCommands() {
		return commands.toArray(new IRCCommand[commands.size()]);
	}

	@Override
	public void onMessageReceived(MessageEvent event) {
		executeCommands(event.getMessage());
	}

	public void executeCommands(Message message) {
		synchronized(commandsLock) {
			try {
				IRCCommand[] validCommands = getCommandsForMessage(message);
				for(IRCCommand command : validCommands) {
					Message newMessage = message;
					if(message instanceof UserMessage)
						newMessage = createNewMessage((UserMessage) message,
								command);
					command.execute(newMessage);
				}
			} catch(Exception exception) {
				logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
			}
		}
	}

	private IRCCommand[] getCommandsForMessage(Message message) {
		Vector<IRCCommand> validCommands = new Vector<IRCCommand>();
		for(IRCCommand command : commands)
			if(commandValid(message, command))
				validCommands.add(command);
		return validCommands.toArray(new IRCCommand[validCommands.size()]);
	}

	private boolean commandValid(Message message, IRCCommand command) {
		boolean enabled = command.isEnabled(), permitted = true, isCommand = true;
		PermissionsHandler permissionsHandler = bot.getPermissionsHandler();

		if(command.getCommandName() != null && message instanceof UserMessage) {
			UserMessage userMessage = (UserMessage) message;
			permitted = permissionsHandler.isPermitted(userMessage.getSender()
					.getNickname(), command.getPermissions());
			String messageText = userMessage.getMessage();
			messageText = messageText.toLowerCase();
			NicknameHandler nicknameHandler = bot.getNicknameHandler();
			String nickname = nicknameHandler.getNickname();
			if(messageText.startsWith(nickname.toLowerCase() + ": "))
				messageText = messageText.substring((nickname + ": ").length());
			String commandName = command.getCommandName();
			commandName = commandName.toLowerCase();
			isCommand = messageText.startsWith(commandActivator + commandName);
		} else if(message instanceof UserMessage)
			permitted = permissionsHandler.isPermitted(((UserMessage) message)
					.getSender().getNickname(), command.getPermissions());
		return enabled && permitted && isCommand;
	}

	private Message createNewMessage(UserMessage message, IRCCommand command) {
		String createdMessage = createMessage(message, command);
		return new UserMessage(message.getType(), message.getRaw(),
				message.getSender(), message.getReceiver(),
				createdMessage, message.isCTCP());
	}

	private String createMessage(UserMessage message, IRCCommand command) {
		String messageText = message.getMessage();
		String messageLC = messageText.toLowerCase();
		if(command.getCommandName() != null) {
			NicknameHandler nicknameHandler = bot.getNicknameHandler();
			String nickname = nicknameHandler.getNickname();
			if(messageLC.startsWith(nickname.toLowerCase() + ": ")) {
				int index = (nickname + ": ").length();
				messageText = messageText.substring(index);
				messageLC = messageLC.substring(index);
			}
			String commandName = command.getCommandName();
			commandName = commandName.toLowerCase();
			String commandString = commandActivator + commandName;
			if(messageLC.startsWith(commandString))
				messageText = messageText.substring(commandString.length());
		}
		return messageText;
	}

	@Override
	public void onMessageSent(MessageEvent event) {
	}

	@Override
	public void onRawSent(MessageEvent event) {
	}

	@Override
	public void onNoticeSent(MessageEvent event) {
	}

	@Override
	public String getName() {
		return "CommandHandler";
	}

	public String getCommandActivator() {
		return commandActivator;
	}

	public void setCommandActivator(String commandActivator) {
		this.commandActivator = commandActivator;
	}

}
