package org.darkstorm.minecraft.darkbot.protocol;

import org.darkstorm.minecraft.darkbot.MinecraftBot;

public abstract class ProtocolProviderX<T extends ProtocolX<?>> extends ProtocolProvider<T> {
	@Override
	public abstract T getProtocolInstance(MinecraftBot bot);

}
