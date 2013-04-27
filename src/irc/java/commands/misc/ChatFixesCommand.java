package commands.misc;

import java.util.regex.Pattern;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.CommandHandler;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class ChatFixesCommand extends IRCCommand {
	private final UserMessage[] lastMessages = new UserMessage[10];
	private final String sedReplaceRegex, sedDeleteRegex,
			additionSubtractionRegex, correctionRegex, multiCorrectionRegex;

	public ChatFixesCommand(CommandHandler commandHandler) {
		super(commandHandler);
		sedReplaceRegex = "s/[^/]*/[^/]*/g?";
		sedDeleteRegex = "([0-9]*|/[^/]*/),([0-9]*|/[^/]*/)d";
		additionSubtractionRegex = "^[+-].";
		correctionRegex = "[^\\* ]*\\*";
		multiCorrectionRegex = "([^ \\*]*\\*[ ])*[^ \\*]*\\*";
	}

	@Override
	public void execute(Message message) {
		if(!(message instanceof UserMessage))
			return;
		UserMessage userMessage = (UserMessage) message;
		if(userMessage.getSender().getNickname()
				.equals(bot.getNicknameHandler().getNickname()))
			return;
		String msg = userMessage.getMessage();
		if(msg.matches(sedReplaceRegex)) {
			String regex = msg.split("/")[1];
			Pattern pattern = Pattern.compile(regex);
			String replacement = msg.split("/")[2];
			boolean repeat = msg.endsWith("g");
			for(UserMessage lastMessage : lastMessages) {
				if(lastMessage == null)
					break;
				String lastMsg = lastMessage.getMessage();
				if(pattern.matcher(lastMsg).find()) {
					String fixedMessage = repeat ? lastMsg.replaceAll(regex,
							replacement) : lastMsg.replaceFirst(regex,
							replacement);
					bot.getMessageHandler().sendMessage(
							Tools.getCorrectTarget(userMessage),
							"<" + lastMessage.getSender().getNickname()
									+ "> " + fixedMessage);
					break;
				}
			}
		} else if(msg.matches(sedDeleteRegex)) {
			String begin = msg.split(",")[0];
			String end = msg.split(",")[1];
			end = end.substring(0, end.length() - 1);
			for(UserMessage lastMessage : lastMessages) {
				if(lastMessage == null)
					break;
				String lastMsg = lastMessage.getMessage();
				if(lastMessage.getSender().equals(userMessage.getSender())) {
					int beginIndex, endIndex;
					if(begin.startsWith("/"))
						beginIndex = lastMsg.indexOf(begin.substring(1,
								begin.length() - 1)) + 1;
					else
						beginIndex = Integer.parseInt(begin);
					if(end.startsWith("/"))
						endIndex = lastMsg.indexOf(end.substring(1,
								end.length() - 1))
								+ (end.length() - 2);
					else
						endIndex = Integer.parseInt(end);
					if(beginIndex < 0 || endIndex < 0)
						break;
					beginIndex = Math.min(beginIndex, lastMsg.length());
					endIndex = Math.min(endIndex, lastMsg.length());
					if(beginIndex > endIndex) {
						int temp = endIndex;
						endIndex = beginIndex;
						beginIndex = temp;
					}
					beginIndex--;
					bot.getMessageHandler().sendMessage(
							Tools.getCorrectTarget(userMessage),
							"<"
									+ lastMessage.getSender().getNickname()
									+ "> "
									+ lastMsg.substring(0, beginIndex)
									+ lastMsg.substring(endIndex,
											lastMsg.length()));
					break;
				}
			}
		} else if(msg.matches(additionSubtractionRegex)) {

		} else if(msg.matches(correctionRegex)) {

		} else if(msg.matches(multiCorrectionRegex)) {

		} else
			updateLastMessages(userMessage);
	}

	private void updateLastMessages(UserMessage message) {
		for(int i = lastMessages.length - 1; i > 0; i--) {
			lastMessages[i] = lastMessages[i - 1];
		}
		lastMessages[0] = message;
	}

	@Override
	public String getName() {
		return "Chat Fixes";
	}

	@Override
	public String getDescription() {
		return "Auto-replaces certain things";
	}

}
