package org.darkstorm.darkbot.minecraftbot.protocol;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;

public abstract class ProtocolProviderX<T extends ProtocolX<?>> extends ProtocolProvider<T> {
	@Override
	public abstract T getProtocolInstance(MinecraftBot bot);

}
