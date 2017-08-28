package org.darkstorm.minecraft.darkbot.wrapper.commands;

import org.darkstorm.minecraft.darkbot.wrapper.MinecraftBotWrapper;

public class LoginCommand extends AbstractCommand {
    public LoginCommand(MinecraftBotWrapper bot) {
        super(bot, "login", "Logs in to an offline server", "password", ".+");
    }

    @Override
    public void execute(String[] args) {
        bot.sendChat("/login " + args[0]);
    }
}
