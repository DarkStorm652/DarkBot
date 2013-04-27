package org.darkstorm.darkbot.ircbot.events;

import org.darkstorm.darkbot.ircbot.irc.dcc.*;

public class DCCEvent extends Event {
	public DCCEvent(DCCHandler source, DCCTransfer dccTransfer) {
		super(source, System.currentTimeMillis(), dccTransfer);
	}

	@Override
	public DCCHandler getSource() {
		return (DCCHandler) super.getSource();
	}

	public DCCTransfer getDCCTransfer() {
		return (DCCTransfer) argument;
	}
}
