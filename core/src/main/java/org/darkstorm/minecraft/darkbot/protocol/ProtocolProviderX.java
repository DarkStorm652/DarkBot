package org.darkstorm.minecraft.darkbot.protocol;

import org.darkstorm.minecraft.darkbot.MinecraftBot;

public abstract class ProtocolProviderX<T extends ProtocolX<H>, H extends PacketHeader> extends ProtocolProvider<T, H> {
	@Override
	public abstract T getProtocolInstance(MinecraftBot bot);

}
