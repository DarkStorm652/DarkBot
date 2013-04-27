package commands;
import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class MuteCommand extends IRCCommand {
	public MuteCommand(CommandHandler commandHandler) {
		super(commandHandler);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		messageText = messageText.toLowerCase();
		MessageHandler messageHandler = bot.getMessageHandler();
		if(messageText.equals("enable") || messageText.equals("on"))
			if(!messageHandler.isMuteEnabled()) {
				messageHandler.sendMessage(Tools.getCorrectTarget(userMessage),
						"Mute enabled");
				messageHandler.setMuteEnabled(true);
			} else if(messageText.equals("disable")
					|| messageText.equals("off"))
				if(messageHandler.isMuteEnabled()) {
					messageHandler.setMuteEnabled(false);
					messageHandler.sendMessage(Tools
							.getCorrectTarget(userMessage), "Mute disabled");
				} else
					messageHandler.sendMessage(Tools
							.getCorrectTarget(userMessage),
							"Mute is already disabled");
	}

	@Override
	public String getName() {
		return "Mute Command";
	}

	@Override
	public String getCommandName() {
		return "MUTE ";
	}

	@Override
	public String getUsage() {
		return "MUTE <enable|disable|on|off>";
	}

	@Override
	public String getDescription() {
		return "Mutes the bot";
	}

	@Override
	public Permissions getPermissions() {
		return Permissions.PRIVILEGED;
	}

}
