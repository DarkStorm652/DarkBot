package commands;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.events.*;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.handlers.PermissionsHandler.Permissions;
import org.darkstorm.darkbot.ircbot.irc.dcc.DCCFileTransfer;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.UserInfo;

public class DCCFileTransferAcceptCommand extends IRCCommand implements
		DCCListener {

	public DCCFileTransferAcceptCommand(CommandHandler commandHandler) {
		super(commandHandler);
		bot.getEventHandler().addDCCListener(this);
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage)
				|| !((UserMessage) message).isCTCP())
			return;
		UserMessage userMessage = (UserMessage) message;
		String messageText = userMessage.getMessage();
		if(messageText.startsWith("DCC ")) {
			UserInfo sender = userMessage.getSender();
			if(bot.getPermissionsHandler().isPermitted(sender.getNickname(),
					Permissions.OWNER))
				bot.getDCCHandler()
						.processRequest(sender.getNickname(),
								sender.getUsername(), sender.getHostname(),
								messageText);
		}
	}

	@Override
	public String getName() {
		return "DCC File Transfer Accepter";
	}

	@Override
	public String getDescription() {
		return "Accepts all DCC file transfers";
	}

	@Override
	public void onIncomingFileTransfer(DCCEvent event) {
		DCCFileTransfer transfer = (DCCFileTransfer) event.getDCCTransfer();
		transfer.receive(transfer.getFile(), false);
	}

	@Override
	public void onIncomingChatRequest(DCCEvent event) {
	}

	@Override
	public void onFileTransferFinished(DCCEvent event) {
		DCCFileTransfer transfer = (DCCFileTransfer) event.getDCCTransfer();
		Exception exception = transfer.getException();
		if(exception != null)
			bot.getMessageHandler().sendMessage(transfer.getNick(),
					"Transfer failed: " + exception);
		else
			bot.getMessageHandler().sendMessage(transfer.getNick(),
					"Transfer complete!");
	}

}
