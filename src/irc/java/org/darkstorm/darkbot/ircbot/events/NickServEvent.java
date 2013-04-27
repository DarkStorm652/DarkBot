package org.darkstorm.darkbot.ircbot.events;

import org.darkstorm.darkbot.ircbot.handlers.NicknameHandler;

public class NickServEvent extends Event {
	public static final int IDENTIFIED = 0;
	public static final int NICKNAME_CHANGED = 1;

	public NickServEvent(NicknameHandler source, String password) {
		this(source, IDENTIFIED, null, null, password);
	}

	public NickServEvent(NicknameHandler source, String oldNickname,
			String newNickname) {
		this(source, NICKNAME_CHANGED, oldNickname, newNickname, null);
	}

	private NickServEvent(NicknameHandler source, int id, String oldNickname,
			String newNickname, String password) {
		super(source, System.currentTimeMillis(), new Object[] { id,
				oldNickname, newNickname, password });
	}

	@Override
	public NicknameHandler getSource() {
		return (NicknameHandler) super.getSource();
	}

	public int getId() {
		return ((Integer) getArgumentAt(0)).intValue();
	}

	public String getOldNickname() {
		return (String) getArgumentAt(1);
	}

	public String getNewNickname() {
		return (String) getArgumentAt(2);
	}

	public String getPassword() {
		return (String) getArgumentAt(3);
	}
}
