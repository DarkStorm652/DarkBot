package org.darkstorm.darkbot.ircbot.handlers;

import java.util.Vector;

import org.darkstorm.darkbot.ircbot.*;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class PermissionsHandler extends IRCHandler {
	public enum Permissions {
		OWNER,
		ORIGINAL_OWNER,
		PRIVILEGED,
		ALL
	}

	private final String originalOwner;

	private String owner;
	private Vector<String> privileged;

	public PermissionsHandler(IRCBot bot, IRCBotData botInfo) {
		super(bot);
		owner = botInfo.owner;
		originalOwner = owner;
		privileged = new Vector<String>();
	}

	@Override
	public String getName() {
		return "PermissionsHandler";
	}

	public boolean isPermitted(String nickname, Permissions permissionsRequired) {
		if(nickname == null)
			throw new NullPointerException();
		if(permissionsRequired == null)
			return true;
		String nicknameLC = nickname.toLowerCase();
		String ownerLC = owner.toLowerCase();
		switch(permissionsRequired) {
		case ALL:
			return true;
		case OWNER:
			return nicknameLC.equals(ownerLC);
		case ORIGINAL_OWNER:
			return nicknameLC.equals(originalOwner.toLowerCase());
		case PRIVILEGED:
			if(nicknameLC.equals(ownerLC))
				return true;
			return Tools.containsIgnoreCase(privileged, nickname);
		}
		return false;
	}

	public String getOriginalOwner() {
		return originalOwner;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		if(owner == null)
			throw new NullPointerException();
		this.owner = owner;
	}

	public String[] getPrivilegedNicknames() {
		return privileged.toArray(new String[privileged.size()]);
	}

	public void addPrivilegedNick(String nickname) {
		if(nickname == null)
			throw new NullPointerException();
		if(!Tools.containsIgnoreCase(privileged, nickname))
			privileged.add(nickname);
	}

	public boolean removePrivilegedNick(String nickname) {
		if(nickname == null)
			throw new NullPointerException();
		return Tools.removeIgnoreCase(privileged, nickname);
	}

}
