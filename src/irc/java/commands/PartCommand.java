package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class PartCommand extends IRCCommand {

	public PartCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		String receiver = userMessage.getReceiver();
		ChannelHandler channelHandler = bot.getChannelHandler();
		String channelName;
		if(messageText.trim().length() == 0 && Channel.isChannel(receiver))
			channelName = receiver;
		else if(Channel.isChannel(messageText) && !messageText.contains(" "))
			channelName = messageText;
		else
			return;
		MessageHandler messageHandler = bot.getMessageHandler();
		Channel channel = channelHandler.getChannel(channelName);
		if(channel != null) {
			channel.part();
			if(!channelName.equals(receiver))
				messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
						"Parted from " + channelName);
			else
				messageHandler.sendMessage(userMessage.getSender()
						.getNickname(), "Parted from " + channelName);
		} else
			messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
					"Not in channel " + channelName);
	}

	@Override
	public String getName() {
		return "Part Command";
	}

	@Override
	public String getCommandName() {
		return "PART";
	}

	@Override
	public String getUsage() {
		return "PART [channel]";
	}

	@Override
	public String getDescription() {
		return "Parts from a specified channel";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}

}
