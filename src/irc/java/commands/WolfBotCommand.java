package commands;

import java.util.*;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.CommandHandler;
import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.*;

public class WolfBotCommand extends IRCCommand {
	private boolean voiced = false, playing = false, wolf = false;
	private List<String> players = new ArrayList<String>();
	private long startedPlayingTime = 0;
	private Random random;

	public WolfBotCommand(CommandHandler commandHandler) {
		super(commandHandler);
		random = new Random();
	}

	@Override
	public void execute(Message message) {
		if(message instanceof UserMessage) {
			UserMessage userMessage = (UserMessage) message;
			String senderNick = userMessage.getSender().getNickname();
			if(senderNick.equalsIgnoreCase("WolfBot"))
				handleWolfBotMessage(userMessage);
		} else if(message instanceof ModeMessage) {
			ModeMessage modeMessage = (ModeMessage) message;
			if(modeMessage.getMode().startsWith("-v")) {
				for(String target : modeMessage.getTargets())
					if(target.equalsIgnoreCase(bot.getNicknameHandler()
							.getNickname()))
						voiced = false;
			} else if(modeMessage.getMode().startsWith("+v"))
				players.clear();
			for(String target : modeMessage.getTargets()) {
				players.add(target);
				if(target.equalsIgnoreCase(bot.getNicknameHandler()
						.getNickname()))
					voiced = true;
			}
		}
	}

	public void handleWolfBotMessage(UserMessage userMessage) {
		String message = userMessage.getMessage();
		if(!Channel.isChannel(userMessage.getReceiver())) {
			if(message.contains("You are a common villager"))
				wolf = false;
			else if(message.contains("As a wolf")) {
				wolf = true;
				if(playing && players.size() == 3) {
					boolean firstPlayer = random.nextBoolean();
					for(String player : players) {
						if(player.equalsIgnoreCase(bot.getNicknameHandler()
								.getNickname()))
							continue;
						if(firstPlayer) {
							bot.getMessageHandler().sendMessage("WolfBot",
									"!kill " + player);
							break;
						} else
							firstPlayer = true;
					}
				} else if(playing) {
					if(random.nextBoolean()) {
						int playerIndex = random.nextInt(players.size() - 1);
						for(String player : players) {
							if(player.equalsIgnoreCase(bot.getNicknameHandler()
									.getNickname()))
								continue;
							if(playerIndex == 0) {
								bot.getMessageHandler().sendMessage("WolfBot",
										"!kill " + player);
								break;
							} else
								playerIndex--;
						}
					} else
						bot.getMessageHandler().sendMessage("WolfBot", "!rest");
				}
			}
		} else {
			if(message.contains("It is now day") && voiced) {
				playing = true;
				int playerIndex = random.nextInt(players.size());
				for(String player : players) {
					if(playerIndex == 0) {
						bot.getMessageHandler().sendMessage("#wolfbot",
								"!lynch " + player);
						break;
					} else
						playerIndex--;
				}
			} else if(message.contains("Thanks for playing"))
				playing = false;
			else if(message.contains("is dead")) {
				String username = message.split("\\*")[1].split("\\*")[0];
				players.remove(username);
				if(username.equalsIgnoreCase(bot.getNicknameHandler()
						.getNickname()))
					playing = false;
			}
		}

	}

	@Override
	public String getName() {
		return "WolfBot Commands";
	}

	@Override
	public String getDescription() {
		return "Handles WolfBot stuffs";
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
