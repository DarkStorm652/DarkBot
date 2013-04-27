/*
Copyright (c) 2013, DarkStorm (darkstorm@evilminecraft.net)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met: 

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.darkstorm.darkbot;

import java.lang.reflect.Constructor;
import java.util.*;

import joptsimple.OptionSet;

import org.darkstorm.darkbot.bot.*;
import org.darkstorm.darkbot.tools.*;
import org.darkstorm.darkbot.tools.ClassRepository.BotInfo;

public final class DarkBot {
	public static final double VERSION = 1.15;

	private final List<Bot> bots = new ArrayList<Bot>();
	private boolean debugging = false;

	public DarkBot() {
	}

	public DarkBot(OptionSet options) {
		this();
		handleOptions(options);
	}

	private void handleOptions(OptionSet options) {
		if(options.has("debug"))
			debugging = true;
		if(options.has("bot")) {
			String botName = (String) options.valueOf("bot");
			for(BotInfo info : ClassRepository.getBots()) {
				if(botName.equalsIgnoreCase(info.getName())) {
					try {
						Class<? extends BotData> botDataClass = info
								.getBotDataClass();
						BotData botData = botDataClass.newInstance();
						botData.parse(options);
						createBot(botData);
					} catch(Throwable exception) {
						exception.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public Bot createBot(BotData data) {
		Bot bot = null;
		Throwable reason = null;
		for(BotInfo info : ClassRepository.getBots()) {
			try {
				Class<?> botDataClass = info.getBotDataClass();
				if(!botDataClass.isInstance(data))
					continue;
				Class<? extends Bot> botClass = info.getBotClass();
				Constructor<? extends Bot> botConstructor = botClass
						.getConstructor(DarkBot.class, botDataClass);
				bot = botConstructor.newInstance(this, data);
			} catch(Throwable exception) {
				reason = exception;
				break;
			}
		}
		if(bot == null)
			throw new IllegalArgumentException(
					"param 0 (type BotData) is invalid", reason);
		synchronized(bots) {
			bots.add(bot);
		}
		return bot;
	}

	public boolean removeBot(Bot bot) {
		synchronized(bots) {
			return bots.remove(bot);
		}
	}

	public Bot[] getBots() {
		synchronized(bots) {
			return bots.toArray(new Bot[bots.size()]);
		}
	}

	public boolean isDebugging() {
		return debugging;
	}

	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}

}
