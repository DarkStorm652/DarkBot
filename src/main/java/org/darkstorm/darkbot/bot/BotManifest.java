package org.darkstorm.darkbot.bot;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface BotManifest {
	public String name();

	public Class<? extends BotData> botDataClass();
}
