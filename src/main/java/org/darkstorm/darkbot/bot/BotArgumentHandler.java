package org.darkstorm.darkbot.bot;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface BotArgumentHandler {
}
