package org.darkstorm.darkbot.ircbot.events;

public interface NickServListener {
	public void onIdentified(NickServEvent event);

	public void onNicknameChanged(NickServEvent event);
}
